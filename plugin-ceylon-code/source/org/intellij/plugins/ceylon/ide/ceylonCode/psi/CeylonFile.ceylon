import ceylon.interop.java {
    javaClass
}

import com.intellij.extapi.psi {
    PsiFileBase
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.fileTypes {
    FileType
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtil
}
import com.intellij.openapi.util {
    Computable
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    FileViewProvider,
    PsiClassOwner,
    PsiClass
}
import com.intellij.psi.util {
    PsiTreeUtil {
        getChildrenOfType
    }
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
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit,
    LocalAnalysisResult,
    IdePhasedUnit,
    AnyProjectPhasedUnit,
    ProjectPhasedUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Cancellable
}

import java.lang {
    ObjectArray,
    JBoolean=Boolean
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage,
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import com.intellij.psi.text {
    BlockSupport
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}

CeylonLogger<CeylonFile> ceylonFileLogger = CeylonLogger<CeylonFile>();

shared class CeylonFile(FileViewProvider viewProvider)
        extends PsiFileBase(viewProvider, CeylonLanguage.instance) satisfies PsiClassOwner {

    putUserData(BlockSupport.treeDepthLimitExceeded, JBoolean.true);

    value compilationUnitPsi {
        value psi = PsiTreeUtil.findChildOfType(this, javaClass<CeylonPsi.CompilationUnitPsi>());
        assert(exists psi);
        return psi;
    }

    shared Tree.CompilationUnit compilationUnit {
        return compilationUnitPsi.ceylonNode;
    }

    VirtualFile? realVirtualFile() => originalFile.virtualFile;

    shared CeylonLocalAnalyzer? localAnalyzer => 
        let(localAnalyzerManager = project.getComponent(javaClass<CeylonLocalAnalyzerManager>()),
            vf = realVirtualFile())
        if (exists vf) 
        then localAnalyzerManager.get(vf) 
        else null;

    shared ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>? retrieveProjectPhasedUnit() {
        Module? mod = ApplicationManager.application.runReadAction(
            object satisfies Computable<Module> {
                compute() => ModuleUtil.findModuleForPsiElement(outer);
            }
        );

        value ceylonProjects = project.getComponent(javaClass<IdeaCeylonProjects>());
        if (exists ceylonProject = ceylonProjects.getProject(mod),
            exists vf = realVirtualFile(),
            exists ceylonVirtualFile = ceylonProject.projectFileFromNative(vf)) {
            return ceylonProject.getParsedUnit(ceylonVirtualFile);
        }
        return null;
    }

    ExternalPhasedUnit? retrieveExternalPhasedUnit() {
        if (exists externalPuRef = realVirtualFile()?.getUserData(uneditedExternalPhasedUnit),
            exists externalPu = externalPuRef.get()) {

            return externalPu;
        }
        return null;
    }

    shared TypeChecker? typechecker {
        if (isInSourceArchive(realVirtualFile())) {
            value ceylonProjects = project.getComponent(javaClass<IdeaCeylonProjects>());
            if (exists mod = ceylonProjects.findModuleForExternalPhasedUnit(realVirtualFile()),
                exists ceylonProject = mod.ceylonProject) {

                return ceylonProject.typechecker;
            }
            return null;
        } else if (exists projectPhasedUnit = retrieveProjectPhasedUnit()) {
            return projectPhasedUnit.typeChecker;
        }

        return localAnalysisResult?.typeChecker;
    }

    shared LocalAnalysisResult? localAnalysisResult {
        value attachedCompilationUnit = ApplicationManager.application.runReadAction(
            object satisfies Computable<Tree.CompilationUnit> {
                compute() => outer.compilationUnit;
            }
        );

        if (exists la = localAnalyzer,
            exists result = la.result) {

            if (result.parsedRootNode === attachedCompilationUnit) {
                return result;
            } else {
                ceylonFileLogger.warn(() =>
                "LocalAnalysisResult.parsedRootNode (``
                result.parsedRootNode.hash ``) !== ceylonFile.compilationUnit (``
                attachedCompilationUnit.hash `` - `` attachedCompilationUnit.nodeType `` - `` attachedCompilationUnit.location ``) for file `` originalFile.name ``(``originalFile.hash``)");
                return null;
            }
        }

        IdePhasedUnit? phasedUnit = if (!isInSourceArchive(realVirtualFile()))
            then retrieveProjectPhasedUnit()
            else retrieveExternalPhasedUnit();

        if (exists phasedUnit,
            phasedUnit.refinementValidated) {

            if (phasedUnit.compilationUnit === attachedCompilationUnit) {
                return object satisfies LocalAnalysisResult {

                    typecheckedRootNode => attachedCompilationUnit;
                    typeChecker => phasedUnit.typeChecker;
                    tokens => phasedUnit.tokens;
                    parsedRootNode => attachedCompilationUnit;
                    lastPhasedUnit => phasedUnit;
                    lastCompilationUnit => attachedCompilationUnit;

                    shared actual IdeaDocument commonDocument {
                        assert(exists document = viewProvider.document);
                        return IdeaDocument(document);
                    }

                    shared actual BaseCeylonProject? ceylonProject {
                        if (is AnyProjectPhasedUnit phasedUnit) {
                            return phasedUnit.ceylonProject;
                        } else if (is ExternalPhasedUnit phasedUnit) {
                            return phasedUnit.moduleSourceMapper?.ceylonProject;
                        }
                        return null;
                    }
                };
            } else {
                ceylonFileLogger.warn(() => "``
                if(phasedUnit is ExternalPhasedUnit)
                then "ExternalPhasedUnit"
                else "ProjectPhasedUnit"
                ``.compilationUnit (``
                phasedUnit.compilationUnit.hash ``) !== ceylonFile.compilationUnit (``
                attachedCompilationUnit.hash `` - `` attachedCompilationUnit.nodeType `` - `` attachedCompilationUnit.location ``) for file `` originalFile.name ``(``originalFile.hash``)");
                return null;
            }
        }
        platformUtils.log(Status._DEBUG, "localAnalysisResult requested, but was null");
        return null;
    }

    shared actual FileType fileType {
        return CeylonFileType.instance;
    }

    shared PhasedUnit? upToDatePhasedUnit {
        if (exists lar = localAnalysisResult,
            lar.typecheckedRootNode exists) {

            return lar.lastPhasedUnit;
        }
        return null;
    }

    shared PhasedUnit? ensureTypechecked(Cancellable? cancellable = null, Integer timeoutSeconds = 5) {
        if (exists la = localAnalyzer) {
            return la.ensureTypechecked(cancellable, timeoutSeconds)?.lastPhasedUnit;
        }
        return null;
    }

    shared PhasedUnit? forceReparse() {
        onContentReload();

        suppressWarnings("unusedDeclaration")
        value neededToTriggerTheReparse = node.lastChildNode;

        return ensureTypechecked();
    }

    shared actual ObjectArray<PsiClass> classes {
        value classes = ArrayList<PsiClass>();
        if (exists decls = getChildrenOfType(compilationUnitPsi, javaClass<CeylonPsi.DeclarationPsi>())) {
            for (CeylonPsi.DeclarationPsi decl in decls) {
                classes.add(NavigationPsiClass(decl));
            }
        }
        return classes.toArray(ObjectArray<PsiClass>(classes.size()));
    }

    shared actual String packageName => "";

    assign packageName {
    }
}
