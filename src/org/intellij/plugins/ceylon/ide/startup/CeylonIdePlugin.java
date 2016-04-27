package org.intellij.plugins.ceylon.ide.startup;

import com.intellij.diagnostic.PluginException;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CeylonIdePlugin implements ApplicationLoadListener {

    private static File ceylonLanguageArchive;

    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull String s) {
        PluginClassLoader pluginClassLoader = getClassLoader();
        if (pluginClassLoader == null) {
            return;
        }

        // Adds lib/*.car to the classpath
        File[] ceylonArchives = getCeylonArchives();
        for (File carFileToAdd : ceylonArchives) {
            try {
                pluginClassLoader.addURL(carFileToAdd.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        new CeylonIdeMetamodelEnricher().initComponent();
    }

    @Nullable
    public static File getCeylonLanguageArchive() {
        return ceylonLanguageArchive;
    }

    private PluginClassLoader getClassLoader() {
        ClassLoader cl = getClass().getClassLoader();
        if (cl instanceof PluginClassLoader) {
            return (PluginClassLoader) cl;
        }
        return null;
    }

    /**
     * @return an array of CAR/JAR files found in `lib` and `classes/embeddedDist/repo`
     */
    private File[] getCeylonArchives() {
        IdeaPluginDescriptor pluginDescriptor = getPluginDescriptor(getClass());
        final File archiveDirectory = getArchiveDirectory(pluginDescriptor);

        List<File> archives = new ArrayList<>();

        // Load direct children of lib/
        if (archiveDirectory.exists()) {
            archives.addAll(Arrays.asList(archiveDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return StringUtil.endsWithIgnoreCase(name, ".car");
                }
            })));
        }

        // And archives in classes/embeddedDist/repo/
        String[] suffixes = {".car", ".jar"};
        visitDirectory(getEmbeddedCeylonRepository(), archives, suffixes);

        return archives.toArray(new File[archives.size()]);
    }

    private void visitDirectory(File dir, List<File> output, String[] suffixes) {
        for (File child : dir.listFiles()) {
            if (child.isFile()) {
                for (String suffix : suffixes) {
                    if (child.getName().endsWith(suffix)) {
                        output.add(child);

                        if (child.getName().startsWith("ceylon.language-")) {
                            CeylonIdePlugin.ceylonLanguageArchive = child;
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
        File ceylonRepoDir = new File(getClassesDir(), "embeddedDist");
        if (ceylonRepoDir.exists()) {
            return ceylonRepoDir;
        }

        throw new PluginException("Embedded Ceylon system repo not found", PluginId.getId("org.intellij.plugins.ceylon.ide"));
    }

    @NotNull
    public static File getClassesDir() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(CeylonIdePlugin.class));

        if (pluginClassesDir.isDirectory()) {
            return pluginClassesDir;
        }

        throw new PluginException("Plugin's 'classes' directory not found", PluginId.getId("org.intellij.plugins.ceylon.ide"));
    }

    /**
     * @param pluginDescriptor the plugin descriptor
     * @return the <code>lib</code> folder for this plugin
     */
    private File getArchiveDirectory(IdeaPluginDescriptor pluginDescriptor) {
        final File pluginDirectory = pluginDescriptor.getPath();
        return new File(pluginDirectory, "lib");
    }

    private IdeaPluginDescriptor getPluginDescriptor(Class<?> startupClass) {
        PluginId pluginId = PluginManager.getPluginByClassName(startupClass.getName());
        return PluginManager.getPlugin(pluginId);
    }
}
