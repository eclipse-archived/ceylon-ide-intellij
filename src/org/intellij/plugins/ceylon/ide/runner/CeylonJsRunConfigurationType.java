package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

public class CeylonJsRunConfigurationType extends ConfigurationTypeBase {

    protected CeylonJsRunConfigurationType() {
        // using ideaIcons fails in IntelliJ 14...
        super("CeylonJsRunConfiguration", "Ceylon JavaScript",
                "Ceylon JS runner", IconLoader.getIcon("/icons/ceylon.png"));

        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new CeylonRunConfiguration("Ceylon JS", new RunConfigurationModule(project), this);
            }
        });
    }
}
