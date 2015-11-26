package org.intellij.plugins.ceylon.ide.action;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CeylonAddFileAction extends CeylonAddingFilesAction {

    public CeylonAddFileAction() {
        super(ideaIcons_.get_().getFile());
    }

    @Override
    protected void createFiles(final AnActionEvent e, TypeChecker typeChecker, VirtualFile srcRoot, String eventPackage, final PsiDirectory srcRootDir) {
        final VirtualFile eventDir = findEventDir(e);
        String input = Messages.showInputDialog(e.getProject(), message("ceylon.file.wizard.message"), message("ceylon.file.wizard.title"), null, null, new AddFileInputValidator(eventDir));

        if (input != null) {
            final String fileName = getCompleteFileName(input);
            final String unitName = input.endsWith(".ceylon") ? input.substring(0, input.length() - ".ceylon".length()) : input;
            final PsiDirectory subdirectory = PsiManager.getInstance(e.getProject()).findDirectory(eventDir);

            ApplicationManager.getApplication().runWriteAction(new Runnable() {

                @Override
                public void run() {
                    FileTemplateManager templateManager = FileTemplateManager.getInstance(e.getProject());

                    Properties variables = new Properties();
                    variables.put("IS_CLASS", Boolean.toString(Character.isUpperCase(fileName.charAt(0))));
                    variables.put("UNIT_NAME", unitName);

                    try {
                        PsiElement file = FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("unit.ceylon"), fileName, variables, subdirectory);
                        if (file instanceof PsiFile) {
                            ((PsiFile) file).navigate(true);
                        }
                    } catch (Exception e1) {
                        Logger.getInstance(CeylonAddModuleAction.class).error("Can't create file from template", e1);
                    }
                }
            });
        }
    }

    @NotNull
    private String getCompleteFileName(String input) {
        if (input == null) {
            return "";
        }
        return input.endsWith(".ceylon") ? input : input + ".ceylon";
    }

    private class AddFileInputValidator implements InputValidatorEx {
        private VirtualFile directory;

        public AddFileInputValidator(VirtualFile directory) {
            this.directory = directory;
        }

        @Override
        public boolean checkInput(String inputString) {

            return StringUtils.isNotBlank(inputString)
                    && inputString.matches("(\\w|-)+(\\.ceylon)?")
                    && directory.findChild(getCompleteFileName(inputString)) == null;
        }

        @Nullable
        @Override
        public String getErrorText(String inputString) {
            String completeFileName = getCompleteFileName(inputString);

            if (StringUtils.isBlank(inputString)) {
                return message("ceylon.file.wizard.error.blank");
            } else if (!inputString.matches("(\\w|-)+(\\.ceylon)?")) {
                return message("ceylon.file.wizard.error.illegal", inputString);
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
