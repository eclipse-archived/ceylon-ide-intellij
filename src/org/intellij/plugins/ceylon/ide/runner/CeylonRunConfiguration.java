package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
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
import org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Run configuration for Ceylon files.
 */
public class CeylonRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> {

    private String ceylonModule;

    /** Full top level name, including the package, including the module */
    private String topLevelNameFull;

    private Backend backend = Backend.Java;

    public CeylonRunConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory) {
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
                final String repoDir = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
                params.getVMParametersList().add("-Dceylon.system.repo=" + repoDir);

                params.setMainClass("com.redhat.ceylon.launcher.Bootstrap");
                params.getClassPath().add(getBootstrapJarPath());
                params.getProgramParametersList().add(backend == Backend.JavaScript ? "run-js" : "run");
                params.getProgramParametersList().add("--run", topLevelNameFull);
                final Iterable<String> outputPaths = getOutputPaths(CeylonRunConfiguration.this.getProject());
                for (String outputPath : outputPaths) {
                    params.getProgramParametersList().add("--rep", outputPath);
                }
                params.getProgramParametersList().add(getCeylonModuleOrDft());

                params.setWorkingDirectory(getProject().getBasePath()); // todo: make settable
                return params;
            }

            @NotNull
            private String getBootstrapJarPath() {
                PluginId runtimePluginId = PluginManager.getPluginByClassName("org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup");
                assert runtimePluginId != null;
                IdeaPluginDescriptor runtimePlugin = PluginManager.getPlugin(runtimePluginId);
                assert runtimePlugin != null;
                String runtimePluginPath = runtimePlugin.getPath().getAbsolutePath();
                File bootstrapJar = new File(runtimePluginPath,
                        FileUtil.join("classes", "embeddedDist", "lib", "ceylon-bootstrap.jar"));
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
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute("ceylon-module", (String) ObjectUtils.defaultIfNull(getCeylonModule(), ""));
        element.setAttribute("top-level", (String) ObjectUtils.defaultIfNull(getTopLevelNameFull(), ""));
        element.setAttribute("backend", (String) ObjectUtils.defaultIfNull(getBackend().nativeAnnotation, ""));
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

    public void setTopLevelNameFull(String topLevelNameFull) {
        this.topLevelNameFull = topLevelNameFull;
    }

    public String getTopLevelNameFull() {
        return topLevelNameFull;
    }

    public String getCeylonModule() {
        return ceylonModule;
    }

    public String getCeylonModuleOrDft() {
        return isDefaultModule() ? "default" : ceylonModule;
    }

    private boolean isDefaultModule() {
        return ceylonModule == null || ceylonModule.isEmpty();
    }

    public void setCeylonModule(String ceylonModule) {
        this.ceylonModule = ceylonModule;
    }

    public Backend getBackend() {
        return backend;
    }

    public void setBackend(Backend backend) {
        if (backend == null) {
            backend = Backend.Java;
        }
        this.backend = backend;
    }
}
