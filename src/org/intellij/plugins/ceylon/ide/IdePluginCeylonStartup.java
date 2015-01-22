package org.intellij.plugins.ceylon.ide;

import org.intellij.plugins.ceylon.runtime.CeylonRuntime;
import org.intellij.plugins.ceylon.runtime.PluginCeylonStartup;
import org.jetbrains.annotations.NotNull;

/**
 * Created by david on 20/01/15.
 */
public class IdePluginCeylonStartup extends PluginCeylonStartup {
    public IdePluginCeylonStartup(CeylonRuntime ceylonRuntime) {
    }

    @NotNull
    public String getComponentName() {
        return "IdePluginCeylonStartup";
    }
}
