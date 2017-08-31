import com.intellij.execution {
    Executor
}
import com.intellij.execution.actions {
    ConfigurationContext,
    RunConfigurationProducer
}
import com.intellij.execution.configurations {
    ConfigurationFactory,
    RunConfigurationModule,
    RunProfileState,
    JavaCommandLineState,
    JavaParameters,
    ModuleBasedConfiguration,
    SearchScopeProvider,
    ConfigurationType
}
import com.intellij.execution.runners {
    ExecutionEnvironment
}
import com.intellij.openapi.extensions {
    Extensions {
        getExtensions
    }
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.projectRoots {
    Sdk
}
import com.intellij.openapi.roots {
    ModuleRootManager,
    ProjectRootManager
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.openapi.util.io {
    FileUtil
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

import java.io {
    File
}
import java.lang {
    ObjectArray,
    overloaded
}
import java.util {
    Arrays,
    Collection,
    Objects
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProject,
    findModuleByName,
    IdeaCeylonProjects,
    IdeaModule
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}
import org.jdom {
    Element
}

shared class CeylonRunConfigurationProducer(ConfigurationType? configurationType
            = findInstance(getExtensions(ConfigurationType.configurationTypeEp).array, `CeylonRunConfigurationType`))
        extends RunConfigurationProducer<CeylonRunConfiguration>(configurationType) {

    RunConfigParams? getRunConfigParams(PsiElement psiElement) {
        if (is CeylonFile file = psiElement.containingFile,
            exists toplevel = PsiTreeUtil.getTopmostParentOfType(psiElement, `CeylonPsi.DeclarationPsi`)) {
            value ceylonNode = toplevel.ceylonNode;
            if (isRunnable(ceylonNode),
                !exists parent = getTopMostElement(toplevel, ceylonNode),
                exists methodIdentifier = ceylonNode.identifier) {
                value identifier = methodIdentifier.text;
                value cu = file.compilationUnit;
                if (exists pkg = cu.unit?.\ipackage,
                    is IdeaModule mod = pkg.\imodule) {
                    value moduleName = mod.nameAsString;
                    value packageName = pkg.nameAsString;
                    if (exists backend = findBackend(ceylonNode, mod)) {
                        return RunConfigParams {
                            mod = moduleName;
                            pkg = packageName;
                            topLevel = identifier;
                            backend = backend;
                        };
                    }
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

            value pfx = params.pkg.empty then "" else params.pkg + "::";
            value topLevelNameFull = pfx + params.topLevel;
            configuration.topLevelNameFull = topLevelNameFull;
            configuration.setName(topLevelNameFull);
            configuration.ceylonModule = params.mod;
            configuration.backend = params.backend;
            configuration.configurationModule.\imodule = context.\imodule;
            return true;
        }
        else {
            return false;
        }
    }

    function configParams(CeylonRunConfiguration rc) {
        value full = rc.topLevelNameFull;
        value dot = full.lastIndexOf(".");
        return RunConfigParams {
            mod = rc.ceylonModule;
            topLevel = dot<0 then full else full[dot+1...];
            pkg = dot<0 then "" else full[0:dot];
            backend = rc.backend;
        };
    }

    shared actual Boolean isConfigurationFromContext(CeylonRunConfiguration rc, ConfigurationContext context) {
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
                = if (exists model = declaration.declarationModel, !model.nativeBackends.none())
                then declaration.declarationModel.nativeBackends
                else mod.nativeBackends;
        return
            if (backends.none() || backends.supports(Backend.java))
            then Backend.\iJava
            else if (backends.supports(Backend.javaScript)) then Backend.javaScript else null;
    }

    CeylonCompositeElement? getTopMostElement(CeylonPsi.DeclarationPsi toplevel, Tree.Declaration ceylonNode) {
        variable CeylonPsi.DeclarationPsi? current = toplevel;
        while (exists parent
                    = PsiTreeUtil.getParentOfType(current, `CeylonPsi.DeclarationPsi`),
                parent.ceylonNode == ceylonNode) {
            current = parent;
        }
        return current;
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


shared class CeylonRunConfiguration
        (String name, RunConfigurationModule configurationModule, ConfigurationFactory factory)
        extends ModuleBasedConfiguration<RunConfigurationModule>(name, configurationModule, factory) {

    shared variable String ceylonModule = "";
    shared variable String topLevelNameFull = "";
    shared variable String arguments = "";
    shared variable String vmOptions = "";

    shared variable Backend backend = Backend.java;

    shared actual Collection<Module> validModules {
        value modules = ModuleManager.getInstance(project).modules;
        return Arrays.asList(*modules);
    }

    configurationEditor => CeylonRunConfigurationEditor(project);

    shared actual RunProfileState getState(Executor executor, ExecutionEnvironment env)
            => object extends JavaCommandLineState(env) {

                String ceylonModuleOrDft {
                    if (ceylonModule.empty) {
                        return "default";
                    } else if (!ceylonModule.contains("/"),
                            exists mod = findModuleByName(project, ceylonModule)) {
                        return ceylonModule += "/" + mod.version;
                    }
                    else {
                        return ceylonModule;
                    }
                }

                Sdk? projectSdk
                        => if (exists mod = configurationModule.\imodule)
                        then ModuleRootManager.getInstance(mod).sdk
                        else ProjectRootManager.getInstance(configurationModule.project).projectSdk;

                shared actual JavaParameters createJavaParameters() {
                    value projectJdk = projectSdk;
                    value params = JavaParameters();
                    params.jdk = projectJdk;
                    value repoDir = CeylonIdePlugin.embeddedCeylonRepository.absolutePath;
                    params.vmParametersList.add("-Dceylon.system.repo=" + repoDir);
                    params.vmParametersList.addParametersString(vmOptions);
                    params.mainClass = "com.redhat.ceylon.launcher.Bootstrap";
                    params.classPath.add(bootstrapJarPath);
                    params.programParametersList.add(backend == Backend.javaScript then "run-js" else "run");
                    if (exists mod = configurationModule.\imodule,
                        exists ceylonProject = getCeylonProject(mod)) {
                        params.setWorkingDirectory(ceylonProject.ceylonModulesOutputDirectory.parentFile);
                    }
                    params.programParametersList.add("--run", topLevelNameFull);
                    params.programParametersList.add(ceylonModuleOrDft);
                    params.programParametersList.addParametersString(arguments);
                    return params;
                }

                String bootstrapJarPath {
                    value bootstrapJar
                            = File(CeylonIdePlugin.embeddedCeylonDist,
                                    FileUtil.join("lib", "ceylon-bootstrap.jar"));
                    assert (bootstrapJar.\iexists() && !bootstrapJar.directory);
                    return bootstrapJar.absolutePath;
                }

            };

    shared actual void readExternal(Element element) {
        super.readExternal(element);
        ceylonModule = element.getAttributeValue("ceylon-module");
        topLevelNameFull = element.getAttributeValue("top-level");
        backend = Backend.fromAnnotation(element.getAttributeValue("backend"));
        arguments = element.getAttributeValue("arguments");
        vmOptions = element.getAttributeValue("vm-options");
        readModule(element);
    }

    shared actual void writeExternal(Element element) {
        super.writeExternal(element);
        element.setAttribute("ceylon-module", ceylonModule);
        element.setAttribute("top-level", topLevelNameFull);
        element.setAttribute("backend", backend.nativeAnnotation);
        element.setAttribute("arguments", arguments);
        element.setAttribute("vm-options", vmOptions);
        writeModule(element);
    }

    searchScope => SearchScopeProvider.createSearchScope(this.modules);

}

class CeylonFactory(ConfigurationType type)
        extends ConfigurationFactory(type) {
    createTemplateConfiguration(Project project)
            => CeylonRunConfiguration("Ceylon Run Config",
                RunConfigurationModule(project), this);
}

shared class CeylonRunConfigurationType() satisfies ConfigurationType {

    displayName => "Ceylon";

    configurationTypeDescription => "Ceylon class";

    icon => icons.ceylon;

    id => "CeylonRunConfiguration";

    configurationFactories
            => ObjectArray<ConfigurationFactory>.with { CeylonFactory(this) };

}

