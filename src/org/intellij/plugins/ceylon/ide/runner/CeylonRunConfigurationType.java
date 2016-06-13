package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;

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
        return icons_.get_().getCeylon();
    }

    @NotNull
    @Override
    public String getId() {
        return "CeylonRunConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
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
