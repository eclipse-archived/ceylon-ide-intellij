package org.intellij.plugins.ceylon.ide;

import com.intellij.diagnostic.PluginException;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.PathUtil;
import org.intellij.plugins.ceylon.runtime.CeylonRuntime;
import org.intellij.plugins.ceylon.runtime.PluginCeylonStartup;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class IdePluginCeylonStartup extends PluginCeylonStartup implements ApplicationLoadListener {

    @Override
    public void beforeApplicationLoaded(Application application) {
        super.initComponent();
    }

    @Override
    public void initComponent() {
        CeylonRuntime.registerIntellijApiModules();
    }

    @NotNull
    public String getComponentName() {
        return "IdePluginCeylonStartup";
    }

    @NotNull
    public static File getEmbeddedCeylonRepository() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(IdePluginCeylonStartup.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(pluginClassesDir, "repo");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir;
            }
        }

        throw new PluginException("Embedded Ceylon system repo not found", PluginId.getId("org.intellij.plugins.ceylon.ide"));
    }
}
