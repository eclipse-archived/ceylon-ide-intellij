package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import org.jetbrains.annotations.NotNull;

public class CeylonJsRunConfigurationProducer extends CeylonRunConfigurationProducer {

    protected CeylonJsRunConfigurationProducer() {
        super(ContainerUtil.findInstance(Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP),
                CeylonJsRunConfigurationType.class));
    }

    @NotNull
    @Override
    Backend getBackend() {
        return Backend.JavaScript;
    }

    @Override
    boolean isBackendEnabled(CeylonIdeConfig config) {
        return config.getCompileToJs() != null && config.getCompileToJs().booleanValue();
    }
}
