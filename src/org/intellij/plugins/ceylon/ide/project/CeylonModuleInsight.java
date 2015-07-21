package org.intellij.plugins.ceylon.ide.project;

import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ModuleInsight;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.JavaModuleSourceRoot;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.Consumer;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class CeylonModuleInsight extends ModuleInsight {

    public CeylonModuleInsight(ProgressIndicator progress, Set<String> existingModuleNames, Set<String> existingProjectLibraryNames) {
        super(progress, existingModuleNames, existingProjectLibraryNames);
    }

    @Override
    protected ModuleDescriptor createModuleDescriptor(File moduleContentRoot, Collection<DetectedSourceRoot> sourceRoots) {
        return new ModuleDescriptor(moduleContentRoot, CeylonModuleType.getInstance(), sourceRoots);
    }

    @Override
    public boolean isApplicableRoot(DetectedProjectRoot root) {
        return root instanceof JavaModuleSourceRoot;
    }

    @Override
    protected boolean isSourceFile(File file) {
        return file.isFile() && file.getName().endsWith(CeylonFileType.DEFAULT_EXTENSION);
    }

    @Override
    protected void scanSourceFileForImportedPackages(CharSequence chars, Consumer<String> result) {
    }

    @Override
    protected boolean isLibraryFile(String fileName) {
        return false;
    }

    @Override
    protected void scanLibraryForDeclaredPackages(File file, Consumer<String> result) throws IOException {
    }
}
