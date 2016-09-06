package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

/**
 * Run/Debug configuration type for the Dart backend.
 */
public class CeylonDartRunConfigurationType extends ConfigurationTypeBase {

    protected CeylonDartRunConfigurationType() {
        // using ideaIcons fails in IntelliJ 14...
        super("CeylonDartRunConfiguration", "Ceylon Dart",
                "Ceylon Dart runner", IconLoader.getIcon("/icons/ceylon.png"));

        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new CeylonRunConfiguration("Ceylon Dart", new RunConfigurationModule(project), this);
            }
        });
    }
}
