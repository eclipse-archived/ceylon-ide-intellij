package org.intellij.plugins.ceylon.ide;

import com.intellij.diagnostic.PluginException;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.PathUtil;
import org.intellij.plugins.ceylon.runtime.CeylonRuntime;
import org.intellij.plugins.ceylon.runtime.PluginCeylonStartup;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdePluginCeylonStartup extends PluginCeylonStartup implements ApplicationLoadListener {

    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull String configPath) {
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

    @Override
    public File[] getCeylonArchives() {
        List<File> archives = new ArrayList<>();

        // Load direct children of lib/
        archives.addAll(Arrays.asList(super.getCeylonArchives()));
        // And archives in classes/repo/ that were not already loaded by CeylonRuntime
        String[] suffixes = {".car", ".jar"};
        visitDirectory(getEmbeddedCeylonRepository(), archives, suffixes);

        return archives.toArray(new File[archives.size()]);
    }

    private void visitDirectory(File dir, List<File> output, String[] suffixes) {
        PluginClassLoader parentCL = (PluginClassLoader) CeylonRuntime.class.getClassLoader();

        for (File child : dir.listFiles()) {
            if (child.isFile()) {
                for (String suffix : suffixes) {
                    if (child.getName().endsWith(suffix)) {
                        boolean exists = false;
                        // Skip jars/cars that are already loaded by CeylonRuntime
                        for (URL url : parentCL.getUrls()) {
                            if (url.getFile().endsWith(child.getName())) {
                                exists = true;
                            }
                        }
                        // We reload module-resolver because JS and Aether resolvers are in
                        // the child classpath, so we want them to be visible from
                        // com.redhat.ceylon.cmr.impl.Configuration.class.getClassLoader()
                        if (!exists || child.getName().startsWith("com.redhat.ceylon.module-resolver")) {
                            output.add(child);
                        }
                    }
                }
            } else if (child.isDirectory()) {
                visitDirectory(child, output, suffixes);
            }
        }
    }

    @NotNull
    public static File getEmbeddedCeylonRepository() {
        File repoDir = new File(getEmbeddedCeylonDist(), "repo");

        if (repoDir.isDirectory()) {
            return repoDir;
        }

        throw new PluginException("Embedded Ceylon system repo not found", PluginId.getId("org.intellij.plugins.ceylon.ide"));
    }

    @NotNull
    public static File getEmbeddedCeylonDist() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(IdePluginCeylonStartup.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(pluginClassesDir, "embeddedDist");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir;
            }
        }

        throw new PluginException("Embedded Ceylon system repo not found", PluginId.getId("org.intellij.plugins.ceylon.ide"));
    }
}
