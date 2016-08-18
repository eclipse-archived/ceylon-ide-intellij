import ceylon.interop.java {
    javaClass
}

import com.intellij.extapi.psi {
    PsiFileBase
}
import com.intellij.openapi.application {
    ApplicationManager
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
import com.intellij.psi.text {
    BlockSupport
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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit,
    LocalAnalysisResult,
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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}

CeylonLogger<CeylonFile> ceylonFileLogger = CeylonLogger<CeylonFile>();

shared class CeylonFile(FileViewProvider viewProvider)
        extends PsiFileBase(viewProvider, CeylonLanguage.instance)
        satisfies PsiClassOwner {

    value declarationClass = javaClass<CeylonPsi.DeclarationPsi>();
    value compilationUnitClass = javaClass<CeylonPsi.CompilationUnitPsi>();
    value localAnalyzerManagerClass = javaClass<CeylonLocalAnalyzerManager>();
    value ideaCeylonProjectsClass = javaClass<IdeaCeylonProjects>();
    value noPsiClasses = ObjectArray<PsiClass>(0);

    putUserData(BlockSupport.treeDepthLimitExceeded, JBoolean.true);

    value compilationUnitPsi {
        assert (exists psi = PsiTreeUtil.findChildOfType(this, compilationUnitClass));
        return psi;
    }

    shared Tree.CompilationUnit compilationUnit => compilationUnitPsi.ceylonNode;

    VirtualFile? realVirtualFile() => originalFile.virtualFile;

    shared CeylonLocalAnalyzer? localAnalyzer
            => let (localAnalyzerManager = project.getComponent(localAnalyzerManagerClass))
            if (exists vf = realVirtualFile())
            then localAnalyzerManager[vf]
            else null;

    shared ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>? retrieveProjectPhasedUnit() {
        Module? mod = ApplicationManager.application.runReadAction(
            object satisfies Computable<Module> {
                compute() => ModuleUtil.findModuleForPsiElement(outer);
            }
        );

        value ceylonProjects = project.getComponent(ideaCeylonProjectsClass);
        if (exists ceylonProject = ceylonProjects.getProject(mod),
            exists vf = realVirtualFile(),
            exists ceylonVirtualFile = ceylonProject.projectFileFromNative(vf)) {
            return ceylonProject.getParsedUnit(ceylonVirtualFile);
        }
        return null;
    }

    shared ExternalPhasedUnit? retrieveExternalPhasedUnit() {
        if (exists vf = realVirtualFile(),
            exists externalPuRef = vf.getUserData(uneditedExternalPhasedUnit)) {
            return externalPuRef.get();
        }
        return null;
    }

    shared TypeChecker? typechecker {
        if (isInSourceArchive(realVirtualFile())) {
            value ceylonProjects = project.getComponent(ideaCeylonProjectsClass);
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
        value attachedCompilationUnit
                = ApplicationManager.application.runReadAction(
                    object satisfies Computable<Tree.CompilationUnit> {
                        compute() => outer.compilationUnit;
                    });

        if (exists localAnalyzer = this.localAnalyzer,
            exists result = localAnalyzer.result) {

            if (result.parsedRootNode === attachedCompilationUnit) {
                return result;
            }
            else {
                ceylonFileLogger.warn(() =>
                "LocalAnalysisResult.parsedRootNode (``
                result.parsedRootNode.hash ``) !== ceylonFile.compilationUnit (``
                attachedCompilationUnit.hash `` - `` attachedCompilationUnit.nodeType `` - `` attachedCompilationUnit.location ``) for file `` originalFile.name ``(``originalFile.hash``)");
                return null;
            }
        }

        if (exists phasedUnit
                = if (!isInSourceArchive(realVirtualFile()))
                then retrieveProjectPhasedUnit()
                else retrieveExternalPhasedUnit(),
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
                        assert (exists document = viewProvider.document);
                        return IdeaDocument(document);
                    }

                    ceylonProject
                            => if (is AnyProjectPhasedUnit phasedUnit)
                            then phasedUnit.ceylonProject
                            else phasedUnit.moduleSourceMapper?.ceylonProject;
                };
            }
            else {
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

    fileType => CeylonFileType.instance;

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
        if (exists decls = getChildrenOfType(compilationUnitPsi, declarationClass)) {
            value classes = ObjectArray<PsiClass>(decls.size);
            variable value i = 0;
            for (decl in decls) {
                classes.set(i++, NavigationPsiClass(decl));
            }
            return classes;
        }
        else {
            return noPsiClasses;
        }
    }

    shared actual String packageName => "";
    assign packageName {}

}
