package org.intellij.plugins.ceylon.annotator.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

public class AddModuleDependencyFix extends AbstractIntentionAction {

    private final CeylonPackagePath packagePath;

    public AddModuleDependencyFix(CeylonPackagePath packagePath) {
        this.packagePath = packagePath;
    }

    @NotNull
    @Override
    public String getText() {
        return "Add dependency for module " + packagePath.getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        PsiDirectory directory = packagePath.getContainingFile().getContainingDirectory();
        PsiFile moduleFile = (directory == null) ? null : directory.findFile("module.ceylon");

        if (moduleFile == null) {
            // TODO
            Notifications.Bus.notify(new Notification("Ceylon", "TODO", "Create module.ceylon", NotificationType.INFORMATION));
        } else {
            ASTNode node = moduleFile.getNode().findChildByType(CeylonTypes.MODULE_DESCRIPTOR);

            if (node != null) {
                CeylonModuleDescriptor descriptor = (CeylonModuleDescriptor) node.getPsi();
                CeylonFile file = (CeylonFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.ceylon", CeylonLanguage.INSTANCE, "module a '1'{import " + packagePath.getText() + " '0.4';}");

                CeylonImportModule importModule = file.findChildByClass(CeylonModuleDescriptor.class).getImportModuleList().getImportModuleList().get(0);
                descriptor.getImportModuleList().addBefore(importModule, descriptor.getImportModuleList().getLastChild());
            }// TODO else
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
