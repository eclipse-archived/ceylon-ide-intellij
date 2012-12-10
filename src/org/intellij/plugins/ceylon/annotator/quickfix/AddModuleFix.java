package org.intellij.plugins.ceylon.annotator.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.psi.CeylonImportModule;
import org.intellij.plugins.ceylon.repo.RepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * TODO rename AddModuleDependencyFix
 */
public class AddModuleFix extends AbstractIntentionAction {
    private final CeylonImportModule importModule;

    public AddModuleFix(CeylonImportModule importModule) {
        this.importModule = importModule;
    }

    @NotNull
    @Override
    public String getText() {
        return "Add dependency to module " + importModule.getPackagePath().getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Module module = ModuleUtil.findModuleForPsiElement(importModule);
        RepositoryManager repositoryManager = ModuleServiceManager.getService(module, RepositoryManager.class);

        try {
            String version = importModule.getModuleVersion().getText();
            File archive = repositoryManager.getLocalArchive(importModule.getPackagePath().getText(), version.substring(1, version.length() - 1));
            if (archive != null && archive.exists()) {
                ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
                LibraryTable.ModifiableModel modifiableModel = rootModel.getModuleLibraryTable().getModifiableModel();
                VirtualFile libraryPath = VirtualFileManager.getInstance().findFileByUrl("file://" + archive.getAbsolutePath());

                if (libraryPath != null) {
                    Library library = modifiableModel.getLibraryByName("Ceylon libs");

                    if (library == null) {
                        library = modifiableModel.createLibrary("Ceylon libs");
                    }

                    Library.ModifiableModel libModel = library.getModifiableModel();
                    libModel.addRoot(JarFileSystem.getInstance().findFileByPath(libraryPath.getPath() + JarFileSystem.JAR_SEPARATOR), OrderRootType.CLASSES);

                    libModel.commit();
                    modifiableModel.commit();
                    rootModel.commit();
                }
            }
        } catch (Exception e) {
            throw new IncorrectOperationException("Can't find local archive", e);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
