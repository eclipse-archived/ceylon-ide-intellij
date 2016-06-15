import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.codeInsight.daemon {
    DaemonCodeAnalyzer
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiManager
}
import com.intellij.util {
    Alarm
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.model {
    BaseIdeModuleSourceMapper,
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument,
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit,
    EditedPhasedUnit,
    ProjectPhasedUnit,
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    SingleSourceUnitPackage,
    Path
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    DummyFolder,
    SourceCodeVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Cancellable
}

import java.lang {
    Thread
}
import java.util {
    List
}
import java.util.concurrent.locks {
    ReentrantLock
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}

shared class MutableLocalAnalysisResult(
    Document theDocument,
    List<CommonToken> theTokens,
    Tree.CompilationUnit theParsedRootNode,
    shared actual BaseCeylonProject? ceylonProject) 
        satisfies LocalAnalysisResult {
    
    variable CommonDocument commonDocument_;
    variable Document document_;
    variable List<CommonToken> tokens_;
    variable Tree.CompilationUnit parsedRootNode_;
    
    variable TypeChecker? typechecker_ = null;
    variable PhasedUnit? lastPhasedUnit_ = null;

    document_ = theDocument;
    commonDocument_ = IdeaDocument(theDocument);
    tokens_ = theTokens;
    parsedRootNode_ = theParsedRootNode;

    shared void resetParsedDocument(
            Document theDocument,
            Tree.CompilationUnit theParsedRootNode,
            List<CommonToken> theTokens) {
        document_ = theDocument;
        commonDocument_ = IdeaDocument(theDocument);
        tokens_ = theTokens;
        parsedRootNode_ = theParsedRootNode;
    }
    
    shared void finishedTypechecking(
        PhasedUnit phasedUnit, 
        TypeChecker? typechecker) {
        lastPhasedUnit_ = phasedUnit;
        typechecker_ = typechecker;
    }
    
    shared Document document => document_;
    commonDocument => commonDocument_;
    
    shared actual PhasedUnit? lastPhasedUnit => lastPhasedUnit_;
    
    shared actual Tree.CompilationUnit? lastCompilationUnit => 
            lastPhasedUnit?.compilationUnit;
    
    shared actual TypeChecker? typeChecker => typechecker_;
    
    shared actual Tree.CompilationUnit parsedRootNode => parsedRootNode_;
    
    shared actual List<CommonToken> tokens => tokens_;
    
    shared actual Tree.CompilationUnit? typecheckedRootNode => 
            if (exists lastRootNode = lastCompilationUnit,
        lastRootNode === parsedRootNode)
    then lastRootNode
    else null;

    
}

shared variable Integer delayToRetryCancelledLocalAnalysis = 1000;
shared variable Integer delayToStartLocalAnalysisAfterParsing = 200;

shared class CeylonLocalAnalyzer(IdeaCeylonProject? ceylonProject, VirtualFile() virtualFileAccessor, Project() ideaProjectAccessor) {
    variable MutableLocalAnalysisResult? result_= null;
    value resultLock = ReentrantLock(true);
    variable value backgroundAnalysisDisabled = false;
    variable Anything()? taskSubmittedWhileBackgroundTypecheckingWasDisabled = null;
    
    variable value cancelledToRestart_ = false; 
    object restarterCancellable satisfies Cancellable {
        shared actual Boolean cancelled => cancelledToRestart_;
    }
    
    void checkCancelled(Cancellable? cancellable) {
        if (exists cancellable,
            cancellable.cancelled) {
            throw ProcessCanceledException();
        }
    }

    value localTypecheckingAlarm = Alarm(Alarm.ThreadToUse.pooledThread, ApplicationManager.application);

    value virtualFile => virtualFileAccessor();
    value ideaProject => ideaProjectAccessor();
    
    void submitLocalTypechecking(
        void typecheckingTask(), 
        Integer delay = delayToStartLocalAnalysisAfterParsing) {
        
        localTypecheckingAlarm.cancelAllRequests();
        if (backgroundAnalysisDisabled) {
            taskSubmittedWhileBackgroundTypecheckingWasDisabled = typecheckingTask;            
            return;
        }
        cancelledToRestart_ = true;
        localTypecheckingAlarm.addRequest(JavaRunnable(() {
            localTypecheckingAlarm.cancelAllRequests();
            if (backgroundAnalysisDisabled) {
                return;
            }

            cancelledToRestart_ = false;
            try {
                typecheckingTask();
            } catch(ProcessCanceledException e) {
                if (!cancelledToRestart_) {
                    // The local typechecking was cancelled because external conditions made it impossible
                    // source model lock unavailable, global model not typechecked, typechecker null...
                    // => retry later but not too quick.
                    submitLocalTypechecking(typecheckingTask, delayToRetryCancelledLocalAnalysis);
                }
                return;
            }
            if (restarterCancellable.cancelled) {
                return;
            }
            
            void restartDaemonCodeAnalyzer() {
                if (exists psiFile = PsiManager.getInstance(ideaProject).findFile(virtualFile)) {
                    DaemonCodeAnalyzer.getInstance(ideaProject).restart(psiFile);
                }
            }
            try {
                concurrencyManager.needReadAccess(restartDaemonCodeAnalyzer, 0);
            } catch(ProcessCanceledException e) {
                ApplicationManager.application.invokeLater(
                    JavaRunnable(restartDaemonCodeAnalyzer));
            }
        }), delay);
    }
    
    shared void fileChanged() {
        noop();
        // appelé depuis subTreeChanged() : voir si c'est avant ou après le reparsing.
        // Et si c'est avant, faire un cancel du typechecking local en cours,
        // positionner le flag dirty et attendre le reparsing.
        // et faire en sorte que le LocalAnalysisResult soit Null si le dernier code n'est pas parsé.
    }

    function prepareLocalAnalysisResult(Document document, Tree.CompilationUnit parsedRootNode, List<CommonToken> tokens) {
        resultLock.lock();
        try {
            MutableLocalAnalysisResult result;
            if (exists existingResult = result_) {
                result = existingResult;
                result.resetParsedDocument(document, parsedRootNode, tokens);
            }
            else {
                result = MutableLocalAnalysisResult(document, tokens, parsedRootNode, ceylonProject);
                result_ = result;
            }
            return result;
        } finally {
            resultLock.unlock();
        }
    }
    
    shared void parsedProjectSource(Document document, Tree.CompilationUnit parsedRootNode, List<CommonToken> tokens) {
        value result = prepareLocalAnalysisResult(document, parsedRootNode, tokens);
        submitLocalTypechecking(void () {
            typecheckSourceFile(document, parsedRootNode, tokens, result, 0, restarterCancellable);
        });
    }

    shared void translatedExternalSource(Document document, ExternalPhasedUnit phasedUnit) {
        value result = prepareLocalAnalysisResult(document, phasedUnit.compilationUnit, phasedUnit.tokens);
        result.finishedTypechecking(phasedUnit, phasedUnit.typeChecker);
        submitLocalTypechecking(void () {
            completeExternalPhasedUnitTypechecking(phasedUnit, result, restarterCancellable);
            result_ = result;
        });
    }

    "Synchronously perform the typechecking (if necessary)"
    shared PhasedUnit? ensureTypechecked(Cancellable? cancellable, Integer waitForModelInSeconds) {
        backgroundAnalysisDisabled = true;
        try {
            cancelledToRestart_ = true;
            value endWait = system.milliseconds + 5000;
            while(!localTypecheckingAlarm.empty) {
                if (system.milliseconds > endWait) {
                    throw ProcessCanceledException();
                }
                Thread.sleep(100);
            }
            
            Document document;
            Tree.CompilationUnit parsedRootNode;
            List<CommonToken> commonTokens;
            
            resultLock.lock();
            try {
                value currentResult = result_;
                if (! exists currentResult) {
                    return null;
                }
                
                if (currentResult.typecheckedRootNode exists) {
                    return currentResult.lastPhasedUnit;
                }
                document = currentResult.document;
                parsedRootNode = currentResult.parsedRootNode;
                commonTokens = currentResult.tokens;
                typecheckSourceFile(document, parsedRootNode, commonTokens, currentResult, waitForModelInSeconds, cancellable);
                return currentResult.lastPhasedUnit;
            } finally {
                resultLock.unlock();
            }
        } finally {
            backgroundAnalysisDisabled = false;
            if (exists toSubmitAgain = 
                            taskSubmittedWhileBackgroundTypecheckingWasDisabled) {
                taskSubmittedWhileBackgroundTypecheckingWasDisabled = null;
                submitLocalTypechecking(toSubmitAgain);            
            }
        }
    }

    void typecheckSourceFile(Document document, Tree.CompilationUnit parsedRootNode, List<CommonToken> tokens, MutableLocalAnalysisResult result, Integer waitForModelInSeconds, Cancellable? cancellable) {
        if (exists ceylonProject) {
            ceylonProject.withSourceModel(true, () {
                TypeChecker typechecker;
                FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile> fileVirtualFileToTypecheck;
                FolderVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile> srcDir;
                ProjectPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile>? centralModelPhasedUnit;
                Package pkg;

                if (! ceylonProject.typechecked) {
                    throw platformUtils.newOperationCanceledException();
                }
                
                if(exists theTypechecker = ceylonProject.typechecker) {
                    typechecker = theTypechecker;
                } else {
                    throw platformUtils.newOperationCanceledException();
                    // the CeylonProject typechecker is not accesssible: this means that a global model parse is being performed
                }
                
                if (exists sourceFileVirtualFile = ceylonProject.projectFilesMap.get(virtualFile)) {
                    fileVirtualFileToTypecheck = sourceFileVirtualFile;
                    value existingSrcDir = sourceFileVirtualFile.rootFolder;
                    if (! exists existingSrcDir) {
                        platformUtils.log(Status._ERROR, "the `rootFolder` is null for FileVirtualFile: `` sourceFileVirtualFile `` during local typechecking");
                        throw platformUtils.newOperationCanceledException();
                    }
                    srcDir = existingSrcDir;
                    centralModelPhasedUnit = ceylonProject.getParsedUnit(sourceFileVirtualFile);
                    value existingPackage = sourceFileVirtualFile.ceylonPackage;
                    if (! exists existingPackage) {
                        platformUtils.log(Status._ERROR, "The `ceylonPackage` is null for FileVirtualFile: `` sourceFileVirtualFile `` during local typechecking");
                        throw platformUtils.newOperationCanceledException();
                    }
                    pkg = existingPackage;
                } else {
                    fileVirtualFileToTypecheck = SourceCodeVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile> (document.text, Path(virtualFile.path), ceylonProject.ideArtifact, virtualFile, virtualFile.charset.name());
                    srcDir = DummyFolder<Module, VirtualFile, VirtualFile, VirtualFile>(virtualFile.parent?.path else "");
                    pkg = typechecker.context.modules.defaultModule.packages.get(0);
                    centralModelPhasedUnit = null;
                }
                result.finishedTypechecking {
                    typechecker = typechecker; 
                    phasedUnit = createPhasedUnitAndTypecheck(typechecker, parsedRootNode, tokens, fileVirtualFileToTypecheck, srcDir, pkg, centralModelPhasedUnit, cancellable); 
                };
            }, waitForModelInSeconds);
        } else {
            // Case of a virtual file that is not linked to any Idea module 
            // though not being from an external source archive
            // (ex. Ceylon document in a non-ceylon project,
            //    document inside a compare editor ?,
            //    ceylon file loaded from anywhere on the disk, etc ...)
            //
            // This is not supported for now, but will be implemented with something like:
            // 
            // typechecker = createStandaloneTypechecker(virtualFile);
            // Module ideaModule = ModuleUtil.getModuleForFile(virtualFile, ideaProject)
            // fileVirtualFileToTypecheck = SourceCodeVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile> (document.text, Path(virtualFile.path), ideaModule, virtualFile, virtualFile.charset.name());
            // srcDir = DummyFolder<Module, VirtualFile, VirtualFile, VirtualFile>(virtualFile.parent?.path else "");
            // pkg = typechecker.context.modules.defaultModule.packages.get(0);
            // centralModelPhasedUnit = null;
            // createPhasedUnitAndTypecheck(typechecker, fileVirtualFileToTypecheck, srcDir, pkg, centralModelPhasedUnit);
        }
    }

    EditedPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile> createPhasedUnitAndTypecheck(
        TypeChecker typechecker,
        Tree.CompilationUnit parsedCompilationUnit,
        List<CommonToken> tokens,
        FileVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> fileVirtualFileToTypecheck, 
        FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> srcDir,
        Package pkg, 
        ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>? centralModelPhasedUnit,
        Cancellable? cancellable) {

        value singleSourceUnitPackage = SingleSourceUnitPackage(pkg, fileVirtualFileToTypecheck.path);
        
        assert(is BaseIdeModuleSourceMapper msm = typechecker.phasedUnits.moduleSourceMapper);
        
        checkCancelled(cancellable);
        
        value editedPhasedUnit = EditedPhasedUnit {
            unitFile = fileVirtualFileToTypecheck;
            srcDir = srcDir;
            cu = parsedCompilationUnit;
            p = singleSourceUnitPackage;
            moduleManager = typechecker.phasedUnits.moduleManager;
            moduleSourceMapper = msm;
            typeChecker = typechecker;
            tokens = tokens;
            savedPhasedUnit = centralModelPhasedUnit;
            project = ceylonProject?.ideArtifact;
            file = fileVirtualFileToTypecheck.nativeResource;
        };
        
        checkCancelled(cancellable);
        
        msm.moduleManager.modelLoader.loadPackageDescriptors();
        
        value phases = [
        editedPhasedUnit.validateTree,
        () { editedPhasedUnit.visitSrcModulePhase(); },
        editedPhasedUnit.visitRemainingModulePhase,
        editedPhasedUnit.scanDeclarations,
        () { editedPhasedUnit.scanTypeDeclarations(cancellable); },
        editedPhasedUnit.validateRefinement,
        () { editedPhasedUnit.analyseTypes(cancellable); },
        editedPhasedUnit.analyseUsage,
        editedPhasedUnit.analyseFlow
        ];
        
        checkCancelled(cancellable);
        
        for (phase in phases) {
            phase();
            checkCancelled(cancellable);
        }
        
        return editedPhasedUnit;
    }

    void completeExternalPhasedUnitTypechecking(ExternalPhasedUnit phasedUnit, MutableLocalAnalysisResult result, Cancellable cancellable) {
        phasedUnit.analyseTypes(cancellable);
        phasedUnit.analyseUsage();
        if (exists typechecker = phasedUnit.typeChecker) {
            result.finishedTypechecking(phasedUnit, typechecker);
        } else {
            platformUtils.log(Status._WARNING, "the `typechecker` of the ExternalPhasedUnit `` phasedUnit `` is null");
        }
    }
    
    shared LocalAnalysisResult? result => result_;
}
