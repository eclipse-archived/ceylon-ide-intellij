package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Run configuration type for Ceylon files.
 */
public class CeylonRunConfigurationType implements ConfigurationType {

    private ConfigurationFactory myFactory;

    public CeylonRunConfigurationType() {
        myFactory = new CeylonFactory(this);
    }

    @Override
    public String getDisplayName() {
        return "Ceylon";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Ceylon class";
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/ceylon.png");
    }

    @NotNull
    @Override
    public String getId() {
        return "CeylonRunConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] {myFactory};
    }

    private static class CeylonFactory extends ConfigurationFactory {

        protected CeylonFactory(@NotNull ConfigurationType type) {
            super(type);
        }

        @Override
        public RunConfiguration createTemplateConfiguration(Project project) {
            return new CeylonRunConfiguration("Ceylon Run Config", new RunConfigurationModule(project), this);
        }
    }
}
