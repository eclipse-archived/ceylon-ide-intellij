import com.intellij.execution {
    Executor
}
import com.intellij.execution.configurations {
    ConfigurationFactory,
    RunConfigurationModule,
    JavaCommandLineState,
    JavaParameters,
    ModuleBasedConfiguration,
    SearchScopeProvider,
    ConfigurationType
}
import com.intellij.execution.runners {
    ExecutionEnvironment
}
import com.intellij.openapi.\imodule {
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
import com.intellij.openapi.util.io {
    FileUtil
}
import com.redhat.ceylon.common {
    Backend
}

import java.io {
    File
}
import java.lang {
    ObjectArray
}
import java.util {
    Arrays
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProject,
    findModuleByName
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


shared class CeylonRunConfiguration
        (name, configurationModule, factory)
        extends ModuleBasedConfiguration<RunConfigurationModule>
                (name, configurationModule, factory) {

    String name;
    RunConfigurationModule configurationModule;
    ConfigurationFactory factory;

    shared variable String ceylonModule = "";
    shared variable String topLevelNameFull = "";
    shared variable String arguments = "";
    shared variable String vmOptions = "";

    shared variable Backend backend = Backend.java;

    validModules => Arrays.asList(*ModuleManager.getInstance(project).modules);

    configurationEditor => CeylonRunConfigurationEditor(project);

    getState(Executor executor, ExecutionEnvironment env)
            => object extends JavaCommandLineState(env) {

                String ceylonModuleOrDft {
                    if (ceylonModule.empty) {
                        return "default";
                    } else if (!"/" in ceylonModule,
                            exists mod = findModuleByName(project, ceylonModule)) {
                        return ceylonModule + "/" + mod.version;
                    }
                    else {
                        return ceylonModule;
                    }
                }

                Sdk? projectSdk
                        => if (exists mod = configurationModule.\imodule)
                        then ModuleRootManager.getInstance(mod).sdk
                        else ProjectRootManager.getInstance(configurationModule.project).projectSdk;

                String bootstrapJarPath {
                    value bootstrapJar
                            = File(CeylonIdePlugin.embeddedCeylonDist,
                                FileUtil.join("lib", "ceylon-bootstrap.jar"));
                    assert (bootstrapJar.\iexists() && !bootstrapJar.directory);
                    return bootstrapJar.absolutePath;
                }

                value command => backend == Backend.javaScript then "run-js" else "run";

                shared actual JavaParameters createJavaParameters() {
                    value params = JavaParameters();
                    params.jdk = projectSdk;
                    value repoDir = CeylonIdePlugin.embeddedCeylonRepository.absolutePath;
                    params.vmParametersList.add("-Dceylon.system.repo=" + repoDir);
                    params.vmParametersList.addParametersString(vmOptions);
                    params.mainClass = "com.redhat.ceylon.launcher.Bootstrap"; //TODO: not typesafe
                    params.classPath.add(bootstrapJarPath);
                    if (exists mod = configurationModule.\imodule,
                        exists ceylonProject = getCeylonProject(mod)) {
                        params.setWorkingDirectory(ceylonProject.ceylonModulesOutputDirectory.parentFile);
                    }
                    value parametersList = params.programParametersList;
                    parametersList.add(command);
                    parametersList.add("--run", topLevelNameFull);
                    parametersList.add(ceylonModuleOrDft);
                    parametersList.addParametersString(arguments);
                    return params;
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

    shared actual late ObjectArray<ConfigurationFactory> configurationFactories
            = ObjectArray.with { CeylonFactory(this) };

}

