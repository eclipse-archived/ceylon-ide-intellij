package org.intellij.plugins.ceylon.ide.action;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.redhat.ceylon.common.Backend;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.intellij.plugins.ceylon.ide.wizard.CreateCeylonModuleWizard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonAddModuleAction extends CeylonAddingFilesAction {

    @Override
    protected void createFiles(final AnActionEvent e, @Nullable  final IdeaCeylonProject project,
                               final VirtualFile srcRoot, final String eventPackage,
                               final PsiDirectory srcRootDir) {

        final CreateCeylonModuleWizard wizard = new CreateCeylonModuleWizard(e.getProject(), project);

        wizard.show();

        if (wizard.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            final String moduleName = wizard.getModuleName();
            boolean hasSuffix = wizard.getCompilationUnitName().endsWith(".ceylon");
            final String unitFileName = hasSuffix
                    ? wizard.getCompilationUnitName()
                    : wizard.getCompilationUnitName() + ".ceylon";
            final String unitName = hasSuffix
                    ? wizard.getCompilationUnitName().substring(0, ".ceylon".length())
                    : wizard.getCompilationUnitName();

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    PsiDirectory subdirectory = findOrCreateModuleDirectory();

                    try {
                        Backend backend;
                        if (project == null) {
                            backend = null;
                        } else if (project.getCompileToJs() && !project.getCompileToJava()) {
                            backend = Backend.JavaScript;
                        } else if (!project.getCompileToJs() && project.getCompileToJava()) {
                            backend = Backend.Java;
                        } else {
                            backend = null;
                        }
                        ceylonFileFactory.createModuleDescriptor(
                                subdirectory, moduleName, wizard.getModuleVersion(),
                                backend
                        );

                        ceylonFileFactory.createPackageDescriptor(subdirectory,
                                moduleName);

                        if (StringUtil.isNotEmpty(moduleName)) {
                            PsiElement run = ceylonFileFactory.createRun(subdirectory, moduleName,
                                    unitFileName, unitName);

                            if (run instanceof PsiFile) {
                                ((PsiFile) run).navigate(true);
                            }
                        }
                    } catch (Exception e1) {
                        Logger.getInstance(CeylonAddModuleAction.class)
                                .error("Can't create file from template", e1);
                    }
                }

                @NotNull
                private PsiDirectory findOrCreateModuleDirectory() {
                    VirtualFile targetDir = srcRoot.findChild(moduleName.replace('.', '/'));
                    return targetDir != null
                            ? PsiManager.getInstance(e.getProject()).findDirectory(targetDir)
                            : DirectoryUtil.createSubdirectories(moduleName, srcRootDir, ".");
                }
            });
        }
    }

    @Override
    protected Icon getCeylonIcon() {
        return icons_.get_().getModules();
    }
}
