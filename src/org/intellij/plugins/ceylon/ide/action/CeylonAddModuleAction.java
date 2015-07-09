package org.intellij.plugins.ceylon.ide.action;

import com.google.common.base.Preconditions;
import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.all;
import static com.redhat.ceylon.ide.validate.NameValidator.packageNameIsLegal;
import static java.util.Arrays.asList;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CeylonAddModuleAction extends CeylonAddingFilesAction {

    public CeylonAddModuleAction() {
        super(AllIcons.Nodes.Artifact);
    }

    @Override
    protected void createFiles(final AnActionEvent e, final TypeChecker typeChecker, final VirtualFile srcRoot, final String eventPackage, final PsiDirectory srcRootDir) {
        // todo: choose module version in the same dialog
        final String moduleName = Messages.showInputDialog(e.getProject(), message("ceylon.module.wizard.message"), message("ceylon.module.wizard.title"), null, null, new AddModuleInputValidator(typeChecker));

        if (moduleName != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    FileTemplateManager templateManager = FileTemplateManager.getInstance(e.getProject());
                    PsiDirectory subdirectory = DirectoryUtil.createSubdirectories(moduleName, srcRootDir, ".");

                    Properties variables = new Properties();
                    variables.put("MODULE_NAME", moduleName);
                    variables.put("MODULE_VERSION", "1.0.0");

                    try {
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("module.ceylon"), "module.ceylon", variables, subdirectory);
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("package.ceylon"), "package.ceylon", variables, subdirectory);
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("run.ceylon"), "run.ceylon", variables, subdirectory);
                    } catch (Exception e1) {
                        Logger.getInstance(CeylonAddModuleAction.class).error("Can't create file from template", e1);
                    }

                    // FIXME com.redhat.ceylon.compiler.typechecker.context.PhasedUnits expects to parse modules from the root folder
                    // so the only way to not parse everything here seems to be by modifying PhaseUnits or injecting a different ModuleManager into it
                    FileSystemVirtualFile virtualFile = new FileSystemVirtualFile(new File(srcRoot.getCanonicalPath()));
                    parseUnit(virtualFile, typeChecker, e.getProject());
                }
            });
        }
    }

    private void parseUnit(final FileSystemVirtualFile virtualFile, final TypeChecker typeChecker, Project project) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Parsing Ceylon compilation units...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                typeChecker.getPhasedUnits().parseUnit(virtualFile);
            }
        });
    }

    private static class AddModuleInputValidator implements InputValidatorEx {
        private final TypeChecker typeChecker;

        public AddModuleInputValidator(TypeChecker typeChecker) {
            Preconditions.checkNotNull(typeChecker);
            this.typeChecker = typeChecker;
        }

        @Override
        public boolean checkInput(String name) {
            return !name.trim().isEmpty() && packageNameIsLegal(name) && !moduleExists(name);
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String name) {
            if (name.trim().isEmpty()) {
                return message("ceylon.module.wizard.error.blank");
            }
            if (!packageNameIsLegal(name)) {
                return message("ceylon.module.wizard.error.illegal", name);
            }
            if (moduleExists(name)) {
                return message("ceylon.module.wizard.error.duplicate", name);
            }
            return null;
        }

        private boolean moduleExists(String moduleName) {
            Set<Module> modules;
            if (all(asList(typeChecker, typeChecker.getContext(), typeChecker.getContext().getModules(),
                            modules = typeChecker.getContext().getModules().getListOfModules()),
                    notNull())) {
                for (Module module : modules) {
                    if (module.getNameAsString().equals(moduleName)) {
                        return true;
                    }
                }
                return false;
            }
            // cannot tell whether the module exists, so let's be safe! Should never happen.
            return true;
        }
    }

}
