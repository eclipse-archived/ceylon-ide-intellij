package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationType;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.extensions.Extensions.getExtensions;
import static com.intellij.util.containers.ContainerUtil.findInstance;

/**
 * Produces run configurations for the Dart VM.
 */
public class CeylonDartRunConfigurationProducer extends CeylonRunConfigurationProducer {

    protected CeylonDartRunConfigurationProducer() {
        super(findInstance(
                getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP),
                CeylonDartRunConfigurationType.class
        ));
    }

    @NotNull
    @Override
    Backend getBackend() {
        return CeylonRunConfiguration.dartBackend;
    }

    @Override
    boolean isBackendEnabled(CeylonIdeConfig config, ConfigurationContext context) {
        return config.getCompileToDart() != null
                && config.getCompileToDart().booleanValue()
                && isCompatibleWithModuleBackends(context);
    }
}
