package org.intellij.plugins.ceylon.project;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.ConfigWriter;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CeylonModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {

    private CeylonConfig config = new CeylonConfig();
    private List<Pair<String,String>> mySourcePaths;

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {

        ContentEntry contentEntry = doAddContentEntry(rootModel);
        if (contentEntry != null) {
            final List<Pair<String,String>> sourcePaths = getSourcePaths();

            if (sourcePaths != null) {
                for (final Pair<String, String> sourcePath : sourcePaths) {
                    String first = sourcePath.first;
                    new File(first).mkdirs();
                    final VirtualFile sourceRoot = LocalFileSystem.getInstance()
                            .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first));
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
                    }
                }
            }

            persistCeylonConfig(contentEntry);
        }

    }

    private void persistCeylonConfig(ContentEntry contentEntry) throws ConfigurationException {
        VirtualFile file = contentEntry.getFile();
        if (file == null) {
            return;
        }

        File rootDir = new File(file.getPath());
        File ceylonDir = new File(rootDir, ".ceylon");
        ceylonDir.mkdirs();
        try {
            ConfigWriter.write(config, new File(ceylonDir, "config"));
        } catch (IOException e) {
            throw new ConfigurationException("Unable to create .ceylon/config");
        }
    }

    @Override
    public ModuleType getModuleType() {
        return CeylonModuleType.getInstance();
    }


    public CeylonConfig getConfig() {
        return config;
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {
        if (mySourcePaths == null) {
            final List<Pair<String, String>> paths = new ArrayList<Pair<String, String>>();
            @NonNls final String path = getContentEntryPath() + File.separator + "source";
            new File(path).mkdirs();
            paths.add(Pair.create(path, ""));
            return paths;
        }
        return mySourcePaths;
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        mySourcePaths = sourcePaths != null? new ArrayList<>(sourcePaths) : null;
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        if (mySourcePaths == null) {
            mySourcePaths = new ArrayList<>();
        }
        mySourcePaths.add(sourcePathInfo);
    }
}
