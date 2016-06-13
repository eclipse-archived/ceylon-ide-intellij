package org.intellij.plugins.ceylon.ide.action;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateFromTemplateAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.ide.common.util.escaping_;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonFileFactory_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CeylonAddFileAction extends CreateFromTemplateAction<DeclarationPsiNameIdOwner> {

    private static final icons_ icons = icons_.get_();
    private ceylonFileFactory_ ceylonFileFactory = ceylonFileFactory_.get_();

    public CeylonAddFileAction() {
        super("", "Create new Ceylon declaration", icons.getCeylon());
    }

    @NotNull
    private String getCompleteFileName(String input) {
        if (input == null) {
            return "";
        }
        return input.endsWith(".ceylon") ? input : input + ".ceylon";
    }

    @Nullable
    @Override
    protected DeclarationPsiNameIdOwner createFile(String name, String templateName, PsiDirectory dir) {
        PsiElement unit = ceylonFileFactory.createUnit(dir, name, name + ".ceylon", templateName);

        if (unit instanceof CeylonFile) {
            return PsiTreeUtil.findChildOfType(unit, DeclarationPsiNameIdOwner.class);
        }
        return null;
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle("Create Ceylon declaration")
                .addKind("Class", icons.getClasses(), "class")
                .addKind("Function", icons.getMethods(), "unit")
                .addKind("Interface", icons.getInterfaces(), "interface")
                .addKind("Object", icons.getObjects(), "object")
                .setValidator(new AddFileInputValidator(directory.getVirtualFile()));
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Creating Ceylon declaration " + newName;
    }

    private class AddFileInputValidator implements InputValidatorEx {
        private VirtualFile directory;

        AddFileInputValidator(VirtualFile directory) {
            this.directory = directory;
        }

        @Override
        public boolean checkInput(String inputString) {

            return StringUtils.isNotBlank(inputString)
                    && inputString.matches("(\\w|-)+")
                    && directory.findChild(getCompleteFileName(inputString)) == null
                    && !escaping_.get_().isKeyword(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String inputString) {
            String completeFileName = getCompleteFileName(inputString);

            if (StringUtils.isBlank(inputString)) {
                return message("ceylon.file.wizard.error.blank");
            } else if (!inputString.matches("(\\w|-)+")) {
                return message("ceylon.file.wizard.error.illegal", inputString);
            } else if (escaping_.get_().isKeyword(inputString)) {
                return "'" + inputString + "' is a reserved keyword";
            } else {

                if (directory.findChild(completeFileName) != null) {
                    return message("ceylon.file.wizard.error.exists", completeFileName);
                }
            }

            return null;
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }
    }
}
