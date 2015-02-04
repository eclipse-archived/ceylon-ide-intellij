package org.intellij.plugins.ceylon.ide;

import com.intellij.util.PathUtil;
import org.intellij.plugins.ceylon.runtime.CeylonRuntime;
import org.intellij.plugins.ceylon.runtime.PluginCeylonStartup;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by david on 20/01/15.
 */
public class IdePluginCeylonStartup extends PluginCeylonStartup {
    public IdePluginCeylonStartup(CeylonRuntime ceylonRuntime) {
    }

    @Override
    public void initComponent() {
        super.initComponent();
        CeylonRuntime.registerIntellijApiModules();

    }

    @NotNull
    public String getComponentName() {
        return "IdePluginCeylonStartup";
    }

    public static File getEmbeddedCeylonRepository() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(IdePluginCeylonStartup.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(pluginClassesDir, "repo");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir;
            }
        }
        return null;
    }
}
