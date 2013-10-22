package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.ObjectUtils;
import org.intellij.plugins.ceylon.sdk.CeylonSdk;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/**
 * Run configuration for Ceylon files.
 */
public class CeylonRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> {

    private String filePath;
    private String topLevelName;

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
        VirtualFile fileToRun = getFilePath();

        if (fileToRun == null) {
            throw new CantRunException("Cannot find file " + filePath);
        }

        return new JavaCommandLineState(env) {
            @Override
            protected JavaParameters createJavaParameters() throws ExecutionException {
                Sdk ceylonSdk = getCeylonSdk();
                JavaParameters params = new JavaParameters();
                params.setJdk(CeylonSdk.getInternalSdk(ceylonSdk));

                params.setMainClass("com.redhat.ceylon.launcher.Launcher");
                params.getClassPath().add(ceylonSdk.getHomeDirectory() + "/lib/ceylon-bootstrap.jar");
                params.getProgramParametersList().add("run");
                params.getProgramParametersList().add("--run", topLevelName);
                params.getProgramParametersList().add("--rep", getOutputPath());
                params.getProgramParametersList().add("default");

                return params;
            }
        };
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        setFilePath(element.getAttributeValue("file-path"));
        setTopLevelName(element.getAttributeValue("top-level"));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute("file-path", (String) ObjectUtils.defaultIfNull(getRawFilePath(), ""));
        element.setAttribute("top-level", (String) ObjectUtils.defaultIfNull(getTopLevelName(), ""));
    }

    private String getOutputPath() {
        Module module = getConfigurationModule().getModule();
        VirtualFile basePath;

        if (module != null) {
            basePath = CompilerModuleExtension.getInstance(module).getCompilerOutputPath();
        } else {
            basePath = CompilerProjectExtension.getInstance(getProject()).getCompilerOutput().findChild(CompilerModuleExtension.PRODUCTION).findChild(getProject().getName());
        }

        return basePath.getCanonicalPath();
    }

    private Sdk getCeylonSdk() {
        Module module = getConfigurationModule().getModule();

        if (module != null) {
            final Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof CeylonSdk) {
                return sdk;
            }
        }

        Sdk projectSdk = ProjectRootManager.getInstance(getConfigurationModule().getProject()).getProjectSdk();
        if (projectSdk != null && projectSdk.getSdkType() instanceof CeylonSdk) {
            return projectSdk;
        }

        return null;
    }

    @Nullable
    private VirtualFile getFilePath() {
        if (filePath == null) return null;
        return LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(filePath));
    }

    public String getRawFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTopLevelName(String topLevelName) {
        this.topLevelName = topLevelName;
    }

    public String getTopLevelName() {
        return topLevelName;
    }
}
