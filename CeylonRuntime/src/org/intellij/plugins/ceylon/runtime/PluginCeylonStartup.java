package org.intellij.plugins.ceylon.runtime;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.text.StringUtil;
import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.cmr.impl.CMRJULLogger;
import com.redhat.ceylon.cmr.impl.FileContentStore;
import com.redhat.ceylon.cmr.impl.FlatRepository;
import com.redhat.ceylon.cmr.spi.StructureBuilder;
import com.redhat.ceylon.common.Constants;
import com.redhat.ceylon.model.cmr.ArtifactResult;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PluginCeylonStartup implements ApplicationComponent {

    private PluginClassLoader getClassLoader(Class<? extends PluginCeylonStartup> startupClass) {
        ClassLoader cl = getClass().getClassLoader();
        if (cl instanceof PluginClassLoader) {
            return (PluginClassLoader) cl;
        }
        return null;
    }

    protected PluginClassLoader getClassLoader() {
        return getClassLoader(getClass());
    }

    private IdeaPluginDescriptor getPluginDescriptor(Class<? extends PluginCeylonStartup> startupClass) {
        PluginId pluginId = PluginManager.getPluginByClassName(startupClass.getName());
        return PluginManager.getPlugin(pluginId);
    }

    private File getArchiveDirectory() {
        IdeaPluginDescriptor pluginDescriptor = getPluginDescriptor(getClass());
        return getArchiveDirectory(pluginDescriptor);
    }

    private File getArchiveDirectory(IdeaPluginDescriptor pluginDescriptor) {
        final File pluginDirectory = pluginDescriptor.getPath();
        return new File(pluginDirectory, "lib");
    }

    private File[] getArchives(IdeaPluginDescriptor pluginDescriptor, FilenameFilter fileNameFilter) {
        final File archiveDirectory = getArchiveDirectory(pluginDescriptor);
        if (archiveDirectory.exists()) {
            return archiveDirectory.listFiles(fileNameFilter);
        }
        return new File[0];
    }

    private File[] getCeylonArchives(IdeaPluginDescriptor pluginDescriptor) {
        final File archiveDirectory = getArchiveDirectory(pluginDescriptor);
        if (archiveDirectory.exists()) {
            return archiveDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return StringUtil.endsWithIgnoreCase(name, ".car");
                }
            });
        }
        return new File[0];
    }

    public File[] getCeylonArchives() {
        IdeaPluginDescriptor pluginDescriptor = getPluginDescriptor(getClass());
        return getCeylonArchives(pluginDescriptor);
    }

    private static final Pattern moduleArchivePattern = Pattern.compile("(.+)-([^\\-]+)\\.(j|J|c|C)ar");

    private ArtifactContext[] getModuleArtifacts() {
        IdeaPluginDescriptor pluginDescriptor = getPluginDescriptor(getClass());
        return getModuleArtifacts(pluginDescriptor);
    }

    private ArtifactContext[] getModuleArtifacts(final IdeaPluginDescriptor pluginDescriptor) {
        final Map<String, ArtifactContext> artifacts = new HashMap<>();
        File[] modulesArchives = getArchives(pluginDescriptor, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Matcher matcher = moduleArchivePattern.matcher(name);
                if (!"ceylon-bootstrap.jar".equals(name)
                        && !name.equals(pluginDescriptor.getPath().getName() + ".jar")
                        && matcher.matches()) {
                    String moduleName = matcher.group(1);
                    String moduleVersion = matcher.group(2);
                    String moduleType = matcher.group(3).equalsIgnoreCase("C") ?
                            ArtifactContext.CAR : ArtifactContext.JAR;
                    if (artifacts.containsKey(moduleName)) {
                        throw new RuntimeException("Ceylon Metamodel Registering failed : several versions of the module '" + moduleName + "' are referenced from the plugin '" + getClass().getName() + "'");
                    }

                    artifacts.put(moduleName, new ArtifactContext(moduleName, moduleVersion, moduleType));
                    return true;
                }
                return false;
            }
        });
        return artifacts.values().toArray(new ArtifactContext[modulesArchives.length]);
    }

    @Override
    public void initComponent() {
        PluginClassLoader pluginClassLoader = getClassLoader(getClass());
        if (pluginClassLoader == null) {
            return;
        }

        File[] ceylonArchives = getCeylonArchives();
        for (File carFileToAdd : ceylonArchives) {
            try {
                pluginClassLoader.addURL(carFileToAdd.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        StructureBuilder structureBuilder = new FileContentStore(getArchiveDirectory());
        FlatRepository flatRepository = new FlatRepository(structureBuilder.createRoot());
        RepositoryManagerBuilder builder = new RepositoryManagerBuilder(getArchiveDirectory(), new CMRJULLogger(), true, Constants.DEFAULT_TIMEOUT, Proxy.NO_PROXY);
        builder.addRepository(flatRepository);
        RepositoryManager repoManager = builder.buildRepository();

        for (ArtifactContext searchedModule : getModuleArtifacts()) {
            ArtifactResult result = repoManager.getArtifactResult(searchedModule);
            if (result == null) {
                System.err.println("Ceylon Metamodel Registering failed : module '" + searchedModule.getName() + "' could not be registered for the plugin '" + getClass().getName() + "'");
            } else {
                CeylonRuntime.registerModule(result, getClassLoader());
            }
        }
    }

    @Override
    public void disposeComponent() {
    }
}
