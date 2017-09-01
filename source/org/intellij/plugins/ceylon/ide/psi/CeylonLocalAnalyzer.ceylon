import com.intellij.codeInsight.daemon {
    DaemonCodeAnalyzer
}
import com.intellij.concurrency {
    AsyncFuture,
    AsyncFutureFactory,
    AsyncFutureResult
}
import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtilCore
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
    PsiManager,
    PsiDocumentManager
}
import com.intellij.util {
    Alarm
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.parser {
    RecognitionError
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Visitor,
    Node
}
import com.redhat.ceylon.ide.common.model {
    BaseIdeModuleSourceMapper,
    cancelDidYouMeanSearch
}
import com.redhat.ceylon.ide.common.platform {
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
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    concurrencyManager,
    getCeylonProjects,
    getModelManager,
    PsiElementGoneException
}
import org.intellij.plugins.ceylon.ide.util {
    CeylonLogger
}


shared variable Integer delayToRetryCancelledLocalAnalysis = 1000;
shared variable Integer delayToStartLocalAnalysisAfterParsing = 200;

CeylonLogger<CeylonLocalAnalyzer> ceylonLocalAnalyzerLogger = CeylonLogger<CeylonLocalAnalyzer>();

shared class CeylonLocalAnalyzer(VirtualFile virtualFile, Project ideaProject)
    satisfies Disposable {
    variable AsyncFutureResult<LocalAnalysisResult>? resultFuture_ = null;
    variable MutableLocalAnalysisResult? result_= null;
    value resultLock = ReentrantLock(true);
    variable value backgroundAnalysisDisabled = false;
    variable Anything()? taskSubmittedWhileBackgroundTypecheckingWasDisabled = null;
    variable value cancelledToRestart_ = false; 
    variable value disposed = false; 
    object restarterCancellable satisfies Cancellable {
        shared actual Boolean cancelled => cancelledToRestart_;
    }
    
    value localTypecheckingAlarm = Alarm(Alarm.ThreadToUse.pooledThread, ideaProject);
    assert (exists model = getCeylonProjects(ideaProject));
    assert (is IdeaCeylonProject? ceylonProject
            = model.getProject(ModuleUtilCore.findModuleForFile(virtualFile, ideaProject)));
    
    shared actual void dispose() {
        disposed = true;
        localTypecheckingAlarm.dispose();
    }

    void checkCancelled(Cancellable? cancellable) {
        if (exists cancellable,
            cancellable.cancelled) {
            throw ProcessCanceledException();
        }
    }

    value logger => ceylonLocalAnalyzerLogger;
    
    function makeTaskId(Anything() typecheckingTask)
            => "[``(typecheckingTask of Object).hash``(``virtualFile.name``)]";

    void submitLocalTypechecking(
        void typecheckingTask(), 
        Integer delay = delayToStartLocalAnalysisAfterParsing) {
        value taskId => makeTaskId(typecheckingTask);
        
        if (disposed) {
            logger.debug(()=>"ERROR : Submitted local typechecking task `` taskId `` while the CeylonLocalAnalyzer is already disposed.
                                  Returning directly");
            return;
        }
        
        if (exists p=ceylonProject,
            ! p.typechecked) {
            logger.debug(()=>"ERROR : Submitted local typechecking task `` taskId `` while the Ceylon project global model is not available.
                                  Returning directly");
            return;
        }
        
        logger.debug(()=>"Submitted local typechecking task `` taskId `` with delay `` delayToStartLocalAnalysisAfterParsing ``");

        if (exists cachedDocument = FileDocumentManager.instance.getCachedDocument(virtualFile),
            PsiDocumentManager.getInstance(ideaProject).isUncommited(cachedDocument)) {
            logger.debug(()=>"ERROR : Submitted local typechecking task `` taskId `` while the document is still uncommitted
                                  => schedule a submit as soon as the document is committed", 20);
            PsiDocumentManager.getInstance(ideaProject).performForCommittedDocument(cachedDocument, () {
                logger.debug(()=>"Submit again the local typechecking task `` taskId `` now the document has been committed");
                submitLocalTypechecking(typecheckingTask, delay);
            });
            return;
        }

        
        localTypecheckingAlarm.cancelAllRequests();
        if (backgroundAnalysisDisabled) {
            taskSubmittedWhileBackgroundTypecheckingWasDisabled = typecheckingTask;            
            logger.debug(()=>"Background Analysis is disabled, don't add the typechecking task `` taskId `` to the alarm");
            return;
        }
        logger.debug(()=>"Setting the `cancelledToRestart_` flag to stop any already-running typechecking task before adding task `` taskId `` to the Alarm");
        cancelledToRestart_ = true;
        logger.debug(()=>"Adding task `` taskId `` to the Alarm");
        localTypecheckingAlarm.addRequest(() {
            logger.debug(()=>"Typechecking task `` taskId `` selected by the Alarm as the running Request");
            localTypecheckingAlarm.cancelAllRequests();
            if (backgroundAnalysisDisabled) {
                taskSubmittedWhileBackgroundTypecheckingWasDisabled = typecheckingTask;
                logger.debug(()=>"Background Analysis is disabled, don't run the typechecking task `` taskId ``");
                return;
            }

            cancelledToRestart_ = false;
            try {
                logger.debug(()=>"Running the typechecking task `` taskId ``");
                typecheckingTask();
            } catch(ProcessCanceledException e) {
                if (!cancelledToRestart_) {
                    logger.debugThrowable(e,
                        ()=>"Typechecking task `` taskId `` received a cancellation exception during its run
                                 => Submitting a new one with delay `` delayToRetryCancelledLocalAnalysis ``");
                    // The local typechecking was cancelled because external conditions made it impossible
                    // source model lock unavailable, global model not typechecked, typechecker null...
                    // => retry later but not too quick.
                    submitLocalTypechecking(typecheckingTask, delayToRetryCancelledLocalAnalysis);
                } else {
                    logger.debug(()=>"Typechecking task `` taskId `` received a cancellation exception during its run to let a new one take place");
                }
                return;
            }
            if (restarterCancellable.cancelled) {
                logger.debug(()=>"Typechecking task `` taskId `` was cancelled with `cancelledToRestart_` during its run to let a new one take place");
                return;
            }
            
            void restartDaemonCodeAnalyzer() {
                if (exists psiFile = PsiManager.getInstance(ideaProject).findFile(virtualFile)) {
                    logger.debug(()=>"Restarting the DaemonCodeAnalyzer after task `` taskId ``");
                    DaemonCodeAnalyzer.getInstance(ideaProject).restart(psiFile);
                }
            }
            try {
                logger.debug(()=>"Try to restart the DaemonCodeAnalyzer synchronously in a read-access section after task `` taskId ``");
                concurrencyManager.needReadAccess(restartDaemonCodeAnalyzer, 0);
            } catch(ProcessCanceledException e) {
                logger.debug(()=>"Read acces was not immediately available after task `` taskId ``
                                      => try to restart the DaemonCodeAnalyzer asynchronously on the Dispath Thread");
                ApplicationManager.application.invokeLater(restartDaemonCodeAnalyzer);
            }
        }, delay);
    }
    
    shared void fileChanged() {
        noop();
        // appelé depuis subTreeChanged() : voir si c'est avant ou après le reparsing.
        // Et si c'est avant, faire un cancel du typechecking local en cours,
        // positionner le flag dirty et attendre le reparsing.
        // et faire en sorte que le LocalAnalysisResult soit Null si le dernier code n'est pas parsé.
    }

    function prepareLocalAnalysisResult(Document document, Tree.CompilationUnit parsedRootNode, List<CommonToken> tokens) {
        logger.trace(()=>"Enter prepareLocalAnalysisResult(``document.hash``, ``parsedRootNode.hash``, ``tokens.hash``) for file ``virtualFile.name``");
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
                if (exists rf = resultFuture_) {
                    rf.set(result);
                }
            }
            return result;
        } finally {
            resultLock.unlock();
            logger.debug(()=>"Exit prepareLocalAnalysisResult(``document.hash``, ``parsedRootNode.hash``, ``tokens.hash``) for file ``virtualFile.name``");
        }
    }
    
    shared void parsedProjectSource(Document document, Tree.CompilationUnit parsedRootNode, List<CommonToken> tokens) {
        logger.trace(()=>"Enter parsedProjectSource(``document.hash``, ``parsedRootNode.hash``, ``tokens.hash``) for file ``virtualFile.name``", 20);
        value result = prepareLocalAnalysisResult(document, parsedRootNode, tokens);
        submitLocalTypechecking(void () {
            typecheckSourceFile(result, 0, restarterCancellable);
        });
        logger.trace(()=>"Exit parsedProjectSource(``document.hash``, ``parsedRootNode.hash``, ``tokens.hash``) for file ``virtualFile.name``");
    }

    shared void translatedExternalSource(Document document, ExternalPhasedUnit phasedUnit) {
        logger.trace(()=>"Enter translatedExternalSource(``document.hash``, ``phasedUnit.hash``) for file ``virtualFile.name``", 20);
        resultLock.lock();
        MutableLocalAnalysisResult result;
        try {
            result = prepareLocalAnalysisResult(document, phasedUnit.compilationUnit, phasedUnit.tokens);
            result.finishedTypechecking(phasedUnit, phasedUnit.typeChecker);
        } finally {
            resultLock.unlock();
        }
        submitLocalTypechecking(void () {
            completeExternalPhasedUnitTypechecking(result, restarterCancellable);
        });
        logger.debug(()=>"Exit translatedExternalSource(``document.hash``, ``phasedUnit.hash``) for file ``virtualFile.name``");
    }

    void removePreviousTypecheckingErrors(Tree.CompilationUnit parsedRootNode) {
        parsedRootNode.visit(object extends Visitor() {
            shared actual void visitAny(Node node) {
                super.visitAny(node);
                value iterator = node.errors.iterator();
                while (iterator.hasNext()) {
                    if (! iterator.next() is RecognitionError) {
                        iterator.remove();
                    }
                }
            }
        });
    }
    
    shared void scheduleForcedTypechecking(Integer delay = 0, Integer waitForModelInSeconds = 0) {
        logger.trace(()=>"Enter scheduleForcedTypechecking(``0``, ``waitForModelInSeconds``) for file ``virtualFile.name``", 20);
        void typecheckingTask() {
            MutableLocalAnalysisResult currentResult;
            
            resultLock.lock();
            try {
                value theResult = result_;
                if (! exists theResult) {
                    return;
                }
                
                currentResult = theResult;
                
                removePreviousTypecheckingErrors(currentResult.parsedRootNode);
            } finally {
                resultLock.unlock();
            }
            
            typecheckSourceFile(currentResult, waitForModelInSeconds, restarterCancellable);
        }
        submitLocalTypechecking(typecheckingTask, delay);
        logger.trace(()=>"Exit scheduleForcedTypechecking(``0``, ``waitForModelInSeconds``) for file ``virtualFile.name``", 20);
    }

    shared LocalAnalysisResult? forceTypechecking(Cancellable? cancellable, Integer waitForModelInSeconds) =>
            runTypechecking(cancellable, waitForModelInSeconds, true);

    shared LocalAnalysisResult? ensureTypechecked(Cancellable? cancellable = null, Integer waitForModelInSeconds = 5) =>
            runTypechecking(cancellable, waitForModelInSeconds, false);
    
    "Synchronously perform the typechecking (if necessary)"
    LocalAnalysisResult? runTypechecking(Cancellable? cancellable, Integer waitForModelInSeconds, Boolean force) {
        logger.debug(()=>"Enter runTypechecking(``cancellable else "<null>"``, ``waitForModelInSeconds``, ``force``) for file ``virtualFile.name``", 10);

        if (disposed) {
            logger.error(()=>"Try to run a local typechecking synchronously while the CeylonLocalAnalyzer is already disposed.
                                  Returning directly");
            return null;
        }
        
        backgroundAnalysisDisabled = true;
        try {
            logger.debug(()=>"Setting the `cancelledToRestart_` flag to stop any already-running typechecking task before running the synchronous  typechecking on file `` virtualFile.name ``");
            cancelledToRestart_ = true;
            value startWait = system.milliseconds;
            value endWait = startWait + 5000;
            
            logger.debug(()=>"Waiting for the Alarm to be empty for file `` virtualFile.name ``");
            while(!localTypecheckingAlarm.empty) {
                if (system.milliseconds > endWait) {
                    logger.debug(()=>"Throwing a ProcessCancelledException because the Alarm queue wasn't empty in 5 seconds for file `` virtualFile.name ``");
                    throw ProcessCanceledException();
                }
                Thread.sleep(100);
            }
            logger.debug(()=>"Finished waiting for the Alarm to be empty for file `` virtualFile.name `` : `` system.milliseconds - startWait `` ms");
            
            MutableLocalAnalysisResult currentResult;
                        
            resultLock.lock();
            try {
                value theResult = result_;
                if (! exists theResult) {
                    return null;
                }
                
                currentResult = theResult;
                
                if (!force && currentResult.typecheckedRootNode exists) {
                    logger.debug(()=>"The file `` virtualFile.name `` is already up-to-date so don't run the synchronous local typechecking");
                    return currentResult.immutable;
                }
                if (force) {
                    removePreviousTypecheckingErrors(currentResult.parsedRootNode);
                }
            } finally {
                resultLock.unlock();
            }
            typecheckSourceFile(currentResult, waitForModelInSeconds, cancellable);
            return currentResult.immutable;
        } catch (PsiElementGoneException e) {
            if (exists modelManager = getModelManager(ideaProject),
                exists projects = getCeylonProjects(ideaProject)) {
                for (p in projects.ceylonProjects) {
                    p.build.requestFullBuild();
                }
                modelManager.scheduleModelUpdate(0);
            } else {
                logger.errorThrowable(e);
            }
            return null;
        } finally {
            backgroundAnalysisDisabled = false;
            if (exists toSubmitAgain = 
                            taskSubmittedWhileBackgroundTypecheckingWasDisabled) {
                taskSubmittedWhileBackgroundTypecheckingWasDisabled = null;
                logger.debug(()=>"Submitting again the typechecking task [``makeTaskId(toSubmitAgain)``] that was cancelled to let synchronous typechecking run for file ``virtualFile.name``");
                submitLocalTypechecking(toSubmitAgain);            
            }
            logger.debug(()=>"Exit runTypechecking(``cancellable else "<null>"``, ``waitForModelInSeconds``, ``force``) for file ``virtualFile.name``");
        }
    }

    void typecheckSourceFile(MutableLocalAnalysisResult result, Integer waitForModelInSeconds, Cancellable? cancellable) {
        assert (exists modelManager = getModelManager(model.ideaProject));
        try {
            modelManager.pauseAutomaticModelUpdate();
            checkCancelled(cancellable);
            if (exists ceylonProject) {
                ceylonProject.withSourceModel(true, () {
                    Document document;
                    Tree.CompilationUnit parsedRootNode;
                    List<CommonToken> tokens;

                    resultLock.lock();
                    try {
                        document = result.document;
                        parsedRootNode = result.parsedRootNode;
                        tokens = result.tokens;
                    } finally {
                        resultLock.unlock();
                    }

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
                    
                    checkCancelled(cancellable);
                    
                    if (exists sourceFileVirtualFile = ceylonProject.projectFilesMap.get(virtualFile)) {
                        fileVirtualFileToTypecheck = sourceFileVirtualFile;
                        value existingSrcDir = sourceFileVirtualFile.rootFolder;
                        if (! exists existingSrcDir) {
                            platformUtils.log(Status._ERROR, "the `rootFolder` is null for FileVirtualFile: `` sourceFileVirtualFile `` during local typechecking");
                            throw platformUtils.newOperationCanceledException();
                        }
    
                        checkCancelled(cancellable);
                        
                        srcDir = existingSrcDir;
                        centralModelPhasedUnit = ceylonProject.getParsedUnit(sourceFileVirtualFile);
    
                        checkCancelled(cancellable);
                        
                        value existingPackage = sourceFileVirtualFile.ceylonPackage;
                        if (! exists existingPackage) {
                            ceylonLocalAnalyzerLogger.error(() => "The `ceylonPackage` is null for FileVirtualFile: `` sourceFileVirtualFile `` during local typechecking");
                            throw platformUtils.newOperationCanceledException();
                        }
                        pkg = existingPackage;
                    } else {
                        fileVirtualFileToTypecheck = SourceCodeVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile> (document.text, Path(virtualFile.path), ceylonProject.ideArtifact, virtualFile, virtualFile.charset.name());
    
                        checkCancelled(cancellable);
                        
                        srcDir = DummyFolder<Module, VirtualFile, VirtualFile, VirtualFile>(virtualFile.parent?.path else "");
    
                        checkCancelled(cancellable);
                        
                        pkg = typechecker.context.modules.defaultModule.packages.get(0);
                        centralModelPhasedUnit = null;
                    }
    
                    checkCancelled(cancellable);
                    
                    result.finishedTypechecking {
                        typechecker = typechecker; 
                        phasedUnit = createPhasedUnitAndTypecheck(typechecker, parsedRootNode, tokens, fileVirtualFileToTypecheck, srcDir, pkg, centralModelPhasedUnit, cancellable); 
                    };
                    
                    if (result.parsedRootNode.errors.empty) {
                        assert (exists modelManager = getModelManager(ceylonProject.model.ideaProject));
                        if (modelManager.modelUpdateWasCannceledBecauseOfSyntaxErrors) {
                            modelManager.scheduleModelUpdate();
                        }
                    }
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
        } finally {
            modelManager.resumeAutomaticModelUpdate();
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

        return concurrencyManager.withAlternateResolution(() {
            checkCancelled(cancellable);
            
            value singleSourceUnitPackage = SingleSourceUnitPackage(pkg, fileVirtualFileToTypecheck.path);
            
            checkCancelled(cancellable);
            
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
            
            value phases = [
            editedPhasedUnit.validateTree,
            () { editedPhasedUnit.visitSrcModulePhase(); },
            editedPhasedUnit.visitRemainingModulePhase,
            editedPhasedUnit.scanDeclarations,
            () { editedPhasedUnit.scanTypeDeclarations(cancelDidYouMeanSearch); },
            editedPhasedUnit.validateRefinement,
            () { editedPhasedUnit.analyseTypes(cancelDidYouMeanSearch); },
            editedPhasedUnit.analyseUsage,
            editedPhasedUnit.analyseFlow
            ];
            
            checkCancelled(cancellable);
            
            for (phase in phases) {
                phase();
                checkCancelled(cancellable);
            }
            
            return editedPhasedUnit;
        });
    }

    void completeExternalPhasedUnitTypechecking(MutableLocalAnalysisResult result, Cancellable cancellable) {
        assert(is ExternalPhasedUnit phasedUnit = result.lastPhasedUnit);
        concurrencyManager.withUpToDateIndexes(() {
            phasedUnit.analyseTypes(cancelDidYouMeanSearch);
            phasedUnit.analyseUsage();
        });
        if (exists typechecker = phasedUnit.typeChecker) {
            result.finishedTypechecking(phasedUnit, typechecker);
        } else {
            ceylonLocalAnalyzerLogger.debug(()=> "the `typechecker` of the ExternalPhasedUnit `` phasedUnit `` is null");
        }
    }
    
    shared LocalAnalysisResult? result => result_;

    shared AsyncFuture<LocalAnalysisResult> waitForResult() {
        resultLock.lock();
        try {
            AsyncFutureResult<LocalAnalysisResult> resultFuture;
            if (exists existingResultFuture = resultFuture_) {
                resultFuture = existingResultFuture;
            }
            else {
                if (exists existingResult = result_) {
                    return AsyncFutureFactory.wrap(existingResult of LocalAnalysisResult);
                } else {
                    resultFuture = AsyncFutureFactory.instance.createAsyncFutureResult<LocalAnalysisResult>();
                    resultFuture_ = resultFuture;
                }
            }
            return resultFuture;
        } finally {
            resultLock.unlock();
        }
    }

    shared void initialize(CeylonLocalAnalyzerManager manager) {
        ceylonLocalAnalyzerLogger.trace(()=> "Enter initialize(`` manager ``)", 10);
        try {
            if (isInSourceArchive(virtualFile)) {
                manager.scheduleExternalSourcePreparation(virtualFile);
            } else {
                ApplicationManager.application.executeOnPooledThread(
                    () => manager.triggerReparse(virtualFile));
            }
        } finally {
            ceylonLocalAnalyzerLogger.trace(() => "Exit initialize(`` manager ``)", 10);
        }
    }
}
