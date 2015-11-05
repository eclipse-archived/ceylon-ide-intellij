package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;

public class CeylonJsRunConfigurationType extends ConfigurationTypeBase {

    protected CeylonJsRunConfigurationType() {
        super("CeylonJsRunConfiguration", "Ceylon JavaScript",
                "Ceylon JS runner", ideaIcons_.get_().getCeylon());

        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new CeylonRunConfiguration("Ceylon JS", new RunConfigurationModule(project), this);
            }
        });
    }
}
