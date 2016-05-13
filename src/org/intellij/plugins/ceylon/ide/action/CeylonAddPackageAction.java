package org.intellij.plugins.ceylon.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.ide.util.DirectoryUtil.createSubdirectories;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;
import static org.intellij.plugins.ceylon.ide.validate.NameValidator.packageNameIsLegal;

public class CeylonAddPackageAction extends CeylonAddingFilesAction {

    @Override
    protected void createFiles(final AnActionEvent e, final IdeaCeylonProject project,
                               final VirtualFile srcRoot, final String eventPackage,
                               final PsiDirectory eventPsiDir) {
        final String packageName = Messages.showInputDialog(e.getProject(),
                message("ceylon.package.wizard.message"),
                message("ceylon.package.wizard.title"), null, eventPackage, new AddPackageInputValidator());

        if (packageName != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    PsiDirectory srcRootDirectory = PsiManager.getInstance(e.getProject())
                            .findDirectory(srcRoot);
                    PsiDirectory subdirectory = createSubdirectories(packageName, srcRootDirectory, ".");

                    if (subdirectory != null) {
                        ceylonFileFactory.createPackageDescriptor(subdirectory, packageName, true);
                    } else {
                        Logger.getInstance(CeylonAddModuleAction.class)
                                .error("Can't create package descriptor: subdirectory is null.");
                    }
                }
            });
        }
    }

    @Override
    protected Icon getCeylonIcon() {
        return ideaIcons_.get_().getPackages();
    }

    private class AddPackageInputValidator implements InputValidatorEx {

        @Override
        public boolean checkInput(String name) {
            return !name.trim().isEmpty() && packageNameIsLegal(name);
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String name) {
            if (name.trim().isEmpty()) {
                return message("ceylon.package.wizard.error.blank");
            } else if (!packageNameIsLegal(name)) {
                return message("ceylon.package.wizard.error.illegal");
            }
            return null;
        }
    }
}
