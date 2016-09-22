package org.intellij.plugins.ceylon.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.CeylonModelManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.ide.util.DirectoryUtil.createSubdirectories;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;
import static org.intellij.plugins.ceylon.ide.validate.NameValidator.packageNameIsLegal;

public class CeylonAddPackageAction extends CeylonAddingFilesAction {

    @Override
    protected void createFiles(final AnActionEvent e, @Nullable final IdeaCeylonProject project,
                               final VirtualFile srcRoot, final String eventPackage,
                               final PsiDirectory eventPsiDir) {

        boolean madeTheChange = false;
        CeylonModelManager modelManager = null;
        if (project != null) {
            modelManager = project.getModel().getIdeaProject().getComponent(CeylonModelManager.class);
        }
        try {
            if (modelManager != null) {
                modelManager.pauseAutomaticModelUpdate();
            }

            final String packageName = Messages.showInputDialog(
                    e.getProject(),
                    message("ceylon.package.wizard.message"),
                    message("ceylon.package.wizard.title"),
                    null,
                    eventPackage,
                    new AddPackageInputValidator(),
                    TextRange.from(eventPackage.length(), 0)
            );

            if (packageName != null) {
                madeTheChange = true;
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        PsiDirectory srcRootDirectory = PsiManager.getInstance(e.getProject())
                                .findDirectory(srcRoot);
                        PsiDirectory subdirectory = createSubdirectories(packageName, srcRootDirectory, ".");

                        if (subdirectory != null) {
                            PsiElement pack = ceylonFileFactory.createPackageDescriptor(subdirectory, packageName, true);

                            if (pack instanceof Navigatable) {
                                ((Navigatable) pack).navigate(true);
                            }
                        } else {
                            Logger.getInstance(CeylonAddModuleAction.class)
                                    .error("Can't create package descriptor: subdirectory is null.");
                        }
                    }
                });
            }
        } finally {
            if (modelManager != null) {
                if (madeTheChange) {
                    modelManager.resumeAutomaticModelUpdate(0);
                } else {
                    modelManager.resumeAutomaticModelUpdate();
                }
            }
        }
    }

    @Override
    protected Icon getCeylonIcon() {
        return icons_.get_().getPackageFolders();
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
