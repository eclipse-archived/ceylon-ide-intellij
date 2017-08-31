import com.intellij.execution.actions {
    ConfigurationContext,
    RunConfigurationProducer
}
import com.intellij.execution.configurations {
    ConfigurationType
}
import com.intellij.openapi.extensions {
    Extensions {
        getExtensions
    }
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.intellij.util.containers {
    ContainerUtil {
        findInstance
    }
}
import com.redhat.ceylon.common {
    Backend
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaModule,
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonCompositeElement,
    CeylonFile
}

shared class CeylonRunConfigurationProducer(ConfigurationType? configurationType
        = findInstance(getExtensions(ConfigurationType.configurationTypeEp).array,
                        `CeylonRunConfigurationType`))
        extends RunConfigurationProducer<CeylonRunConfiguration>(configurationType) {

    RunConfigParams? getRunConfigParams(PsiElement psiElement) {
        if (is CeylonFile file = psiElement.containingFile,
            exists toplevel
                    = PsiTreeUtil.getTopmostParentOfType(psiElement,
                            `CeylonPsi.DeclarationPsi`)) {
            value ceylonNode = toplevel.ceylonNode;
            if (isRunnable(ceylonNode),
                !exists parent = getTopMostElement(toplevel, ceylonNode),
                exists methodIdentifier = ceylonNode.identifier) {
                value identifier = methodIdentifier.text;
                if (exists pkg = file.compilationUnit.unit?.\ipackage,
                    is IdeaModule mod = pkg.\imodule,
                    exists backend = findBackend(ceylonNode, mod)) {
                    return RunConfigParams {
                        moduleName = mod.nameAsString;
                        packageName = pkg.nameAsString;
                        topLevel = identifier;
                        backend = backend;
                    };
                }
            }
        }
        return null;
    }

    shared actual Boolean setupConfigurationFromContext(CeylonRunConfiguration configuration,
            ConfigurationContext context, Ref<PsiElement> sourceElement) {

        if (exists projects = context.project.getComponent(`IdeaCeylonProjects`),
            exists project = projects.getProject(context.\imodule),
            exists params = getRunConfigParams(sourceElement.get())) {

            value topLevelNameFull
                    = let (packageName = params.packageName)
                        (packageName.empty then "" else packageName + "::")
                            + params.topLevel;

            configuration.topLevelNameFull = topLevelNameFull;
            configuration.setName(topLevelNameFull);
            configuration.ceylonModule = params.moduleName;
            configuration.backend = params.backend;
            configuration.configurationModule.\imodule = context.\imodule;
            return true;
        }
        else {
            return false;
        }
    }

    function configParams(CeylonRunConfiguration config) {
        value full = config.topLevelNameFull;
        value dot = full.lastIndexOf(".");
        return RunConfigParams {
            moduleName = config.ceylonModule;
            topLevel = dot<0 then full else full[dot+1...];
            packageName = dot<0 then "" else full[0:dot];
            backend = config.backend;
        };
    }

    shared actual Boolean isConfigurationFromContext(
            CeylonRunConfiguration rc, ConfigurationContext context) {
        if (exists loc = context.location,
            exists params = getRunConfigParams(loc.psiElement)) {
            return params == configParams(rc);
        }
        else {
            return false;
        }
    }

    Backend? findBackend(Tree.Declaration declaration, IdeaModule mod) {
        value backends
                = if (exists model = declaration.declarationModel,
                        !model.nativeBackends.none())
                then model.nativeBackends
                else mod.nativeBackends;
        return
            if (backends.none()) then Backend.java
            else if (backends.supports(Backend.java)) then Backend.java
            else if (backends.supports(Backend.javaScript)) then Backend.javaScript
            else null;
    }

    CeylonCompositeElement? getTopMostElement(CeylonPsi.DeclarationPsi toplevel, Tree.Declaration ceylonNode) {
        variable CeylonPsi.DeclarationPsi? parent = toplevel;
        while (exists p = (parent = PsiTreeUtil.getParentOfType(parent, `CeylonPsi.DeclarationPsi`)),
                p.ceylonNode == ceylonNode) {}
        return parent;
    }

    Boolean isRunnable(Tree.Declaration ceylonNode)
            => if (is Tree.AnyMethod ceylonNode)
            then isRunnableMethod(ceylonNode)
            else ceylonNode is Tree.ClassDefinition;

    Boolean isRunnableMethod(Tree.AnyMethod ceylonMethod)
            => if (exists list = ceylonMethod.parameterLists[0])
            then list.parameters.empty
            else false;

}
