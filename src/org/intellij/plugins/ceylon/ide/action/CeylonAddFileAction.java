package org.intellij.plugins.ceylon.ide.action;

import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateFromTemplateAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.ide.common.util.escaping_;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonFileFactory_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Objects.firstNonNull;
import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CeylonAddFileAction extends CreateFromTemplateAction<PsiElement> {

    private static final icons_ icons = icons_.get_();
    private ceylonFileFactory_ ceylonFileFactory = ceylonFileFactory_.get_();

    public CeylonAddFileAction() {
        super("", message("ceylon.file.wizard.title"), icons.getFile());
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
    protected PsiElement createFile(String name, String templateName, PsiDirectory dir) {
        PsiElement unit = ceylonFileFactory.createUnit(dir, name, name + ".ceylon", templateName);

        PsiElement namedElement = null;

        if (unit instanceof CeylonFile) {
            namedElement = findChildOfType(unit, DeclarationPsiNameIdOwner.class);
        }

        return firstNonNull(namedElement, unit);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle(message("ceylon.file.wizard.title"))
                .addKind("File", icons.getFile(), "unit")
                .addKind("Class", icons.getClasses(), "class")
                .addKind("Function", icons.getMethods(), "function")
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
