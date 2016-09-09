package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.redhat.ceylon.common.Backend;
import org.apache.commons.lang.ObjectUtils;
import org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import static org.intellij.plugins.ceylon.ide.ceylonCode.model.findModuleByName_.findModuleByName;

/**
 * Run configuration for Ceylon files.
 */
class CeylonRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> {

    private String ceylonModule;

    /** Full top level name, including the package, including the module */
    private String topLevelNameFull;

    private String arguments;

    private String vmOptions;

    private Backend backend = Backend.Java;

    CeylonRunConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();

        return Arrays.asList(modules);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends CeylonRunConfiguration> getConfigurationEditor() {
        return new CeylonRunConfigurationEditor(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        // todo: validate

        return new JavaCommandLineState(env) {
            @Override
            protected JavaParameters createJavaParameters() throws ExecutionException {
                Sdk projectJdk = getProjectSdk();
                JavaParameters params = new JavaParameters();
                params.setJdk(projectJdk);
                final String repoDir = CeylonIdePlugin.getEmbeddedCeylonRepository().getAbsolutePath();
                params.getVMParametersList().add("-Dceylon.system.repo=" + repoDir);
                params.getVMParametersList().addParametersString(getVmOptions());

                params.setMainClass("com.redhat.ceylon.launcher.Bootstrap");
                params.getClassPath().add(getBootstrapJarPath());
                params.getProgramParametersList().add(backend == Backend.JavaScript ? "run-js" : "run");
                params.getProgramParametersList().add("--run", topLevelNameFull);
                final Iterable<String> outputPaths = getOutputPaths(CeylonRunConfiguration.this.getProject());
                for (String outputPath : outputPaths) {
                    params.getProgramParametersList().add("--rep", outputPath);
                }
                params.getProgramParametersList().add(getCeylonModuleOrDft());
                params.getProgramParametersList().addParametersString(getArguments());

                params.setWorkingDirectory(getProject().getBasePath()); // todo: make settable
                return params;
            }

            @NotNull
            private String getBootstrapJarPath() {
                File bootstrapJar = new File(CeylonIdePlugin.getEmbeddedCeylonDist(),
                        FileUtil.join("lib", "ceylon-bootstrap.jar"));
                assert bootstrapJar.exists() && !bootstrapJar.isDirectory();
                return bootstrapJar.getAbsolutePath();
            }
        };
    }

/*
    public static File getPluginDir() {
        return PluginManager.getPlugin(PluginId.getId("org.intellij.plugins.ceylon")).getPath();
    }
*/

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        setCeylonModule(element.getAttributeValue("ceylon-module"));
        setTopLevelNameFull(element.getAttributeValue("top-level"));
        setBackend(Backend.fromAnnotation(element.getAttributeValue("backend")));
        setArguments(element.getAttributeValue("arguments"));
        setVmOptions(element.getAttributeValue("vm-options"));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute("ceylon-module", (String) ObjectUtils.defaultIfNull(getCeylonModule(), ""));
        element.setAttribute("top-level", (String) ObjectUtils.defaultIfNull(getTopLevelNameFull(), ""));
        element.setAttribute("backend", (String) ObjectUtils.defaultIfNull(getBackend().nativeAnnotation, ""));
        element.setAttribute("arguments", (String) ObjectUtils.defaultIfNull(getArguments(), ""));
        element.setAttribute("vm-options", (String) ObjectUtils.defaultIfNull(getVmOptions(), ""));
    }

    private static Iterable<String> getOutputPaths(Project project) {
        final Collection<String> paths = new LinkedHashSet<>();
        final CompilerProjectExtension cpe = CompilerProjectExtension.getInstance(project);
        if (cpe != null && cpe.getCompilerOutput() != null) {
            paths.add(cpe.getCompilerOutput().getCanonicalPath());
        }
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            final CompilerModuleExtension cme = CompilerModuleExtension.getInstance(module);
            if (cme != null&& cme.getCompilerOutputPath() != null) {
                paths.add(cme.getCompilerOutputPath().getCanonicalPath());
            }
        }
        return paths;
    }

    private Sdk getProjectSdk() {
        Module module = getConfigurationModule().getModule();

        if (module != null) {
            return ModuleRootManager.getInstance(module).getSdk();
        }

        return ProjectRootManager.getInstance(getConfigurationModule().getProject()).getProjectSdk();
    }

    String getVmOptions() {
        return vmOptions;
    }

    void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    String getArguments() {
        return arguments;
    }

    void setArguments(String arguments) {
        this.arguments = arguments;
    }

    void setTopLevelNameFull(String topLevelNameFull) {
        this.topLevelNameFull = topLevelNameFull;
    }

    String getTopLevelNameFull() {
        return topLevelNameFull;
    }

    String getCeylonModule() {
        return ceylonModule;
    }

    private String getCeylonModuleOrDft() {
        String moduleName;
        if (isDefaultModule()) {
            moduleName = "default";
        } else {
            moduleName = ceylonModule;

            if (!moduleName.contains("/")) {
                com.redhat.ceylon.model.typechecker.model.Module mod
                        = findModuleByName(getProject(), ceylonModule);

                if (mod != null) {
                    moduleName += "/" + mod.getVersion();
                }
            }
        }

        return moduleName;
    }

    private boolean isDefaultModule() {
        return ceylonModule == null || ceylonModule.isEmpty();
    }

    void setCeylonModule(String ceylonModule) {
        this.ceylonModule = ceylonModule;
    }

    Backend getBackend() {
        return backend;
    }

    void setBackend(Backend backend) {
        if (backend == null) {
            backend = Backend.Java;
        }
        this.backend = backend;
    }
}
