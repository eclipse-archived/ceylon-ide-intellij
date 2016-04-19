package org.intellij.plugins.ceylon.ide.startup;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationInfo;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;
import com.redhat.ceylon.cmr.impl.DefaultRepository;
import com.redhat.ceylon.cmr.impl.FileContentStore;
import com.redhat.ceylon.cmr.spi.StructureBuilder;
import com.redhat.ceylon.model.cmr.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin.getEmbeddedCeylonRepository;

class CeylonIdeMetamodelEnricher extends AbstractMetamodelEnricher {

    @Override
    public void initComponent() {
        super.initComponent();
        registerIntellijApiModules();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Ceylon IDE Metamodel enricher";
    }

    @Override
    protected File[] getArchives(IdeaPluginDescriptor pluginDescriptor, FilenameFilter fileNameFilter) {
        List<File> archives = new ArrayList<>();

        archives.addAll(Arrays.asList(super.getArchives(pluginDescriptor, fileNameFilter)));

        String[] suffixes = {".car", ".jar"};

        visitDirectory(getEmbeddedCeylonRepository(), archives, suffixes, fileNameFilter);

        return archives.toArray(new File[archives.size()]);
    }

    @Override
    protected void configureRepositories(File archiveDirectory, RepositoryManagerBuilder builder) {
        super.configureRepositories(archiveDirectory, builder);
        StructureBuilder structureBuilder = new FileContentStore(getEmbeddedCeylonRepository());
        builder.addRepository(new DefaultRepository(structureBuilder.createRoot()));
    }

    private void visitDirectory(File dir, List<File> output, String[] suffixes, FilenameFilter fileNameFilter) {
        for (File child : dir.listFiles()) {
            if (child.isFile()) {
                if (fileNameFilter.accept(dir, child.getName())) {
                    output.add(child);
                }
            } else if (child.isDirectory()) {
                visitDirectory(child, output, suffixes, fileNameFilter);
            }
        }
    }

    private static void registerIntellijApiModules() {
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        ClassLoader cl = applicationInfo.getClass().getClassLoader();
        final String version = applicationInfo.getMajorVersion();
        Class<? extends ClassLoader> loaderClass = cl.getClass();

        List<URL> urls = Collections.emptyList();
        try {
            Method method = loaderClass.getMethod("getUrls");
            Object result = method.invoke(cl);
            if (result instanceof List) {
                urls = (List<URL>) result;
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (final URL url : urls) {
            final String fileName = url.getFile();
            if (fileName.endsWith("openapi.jar") ||
                    fileName.endsWith("util.jar") ||
                    fileName.endsWith("annotations.jar") ||
                    fileName.endsWith("extensions.jar")) {
                ArtifactResult artifactResult = new ArtifactResult() {
                    @Override
                    public String name() {
                        return fileName.replaceAll("\\.jar$", "");
                    }

                    @Override
                    public String version() {
                        return version;
                    }

                    @Override
                    public ImportType importType() {
                        return ImportType.EXPORT;
                    }

                    @Override
                    public ArtifactResultType type() {
                        return ArtifactResultType.CEYLON;
                    }

                    @Override
                    public VisibilityType visibilityType() {
                        return VisibilityType.STRICT;
                    }

                    @Override
                    public File artifact() throws RepositoryException {
                        try {
                            return new File(url.toURI());
                        } catch (URISyntaxException e) {
                            throw new RepositoryException(e);
                        }
                    }

                    @Override
                    public PathFilter filter() {
                        return null;
                    }

                    @Override
                    public List<ArtifactResult> dependencies() throws RepositoryException {
                        return Collections.emptyList();
                    }

                    @Override
                    public String repositoryDisplayString() {
                        return null;
                    }

                    @Override
                    public Repository repository() {
                        return null;
                    }
                };
                registerModule(artifactResult, cl);
            }
        }
    }
}
