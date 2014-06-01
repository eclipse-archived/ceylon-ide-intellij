package org.intellij.plugins.ceylon.action;

import com.google.common.base.Preconditions;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import org.intellij.plugins.ceylon.annotator.TypeCheckerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.all;
import static com.redhat.ceylon.ide.validate.NameValidator.packageNameIsLegal;
import static java.util.Arrays.asList;

public class CeylonAddModuleAction extends AnAction {

    public CeylonAddModuleAction() {
        super(PlatformIcons.PACKAGE_ICON);
    }

    public void actionPerformed(final AnActionEvent e) {
        final VirtualFile eventDir = findEventDir(e);

        if (eventDir != null && e.getProject() != null) {
            TypeCheckerProvider typeCheckerProvider = e.getProject().getComponent(TypeCheckerProvider.class);
            final TypeChecker typeChecker = typeCheckerProvider.getTypeChecker();

            final VirtualFile srcRoot = getSourceRoot(e, eventDir);
            if (srcRoot != null) {
                String eventPath = eventDir.getPath();
                final String srcRootPath = srcRoot.getPath();
                assert eventPath.startsWith(srcRootPath) : eventPath + " not in " + srcRootPath;

                // Make eventPath relative
                eventPath = eventPath.length() <= srcRootPath.length() ? "" : eventPath.substring(srcRootPath.length() + 1);

                final String eventPackage = eventPath.replace('/', '.');
                final PsiDirectory eventPsiDir = PsiManager.getInstance(e.getProject()).findDirectory(eventDir);

                // todo: choose module version in the same dialog
                final String moduleName = Messages.showInputDialog(e.getProject(), "Enter module name", "Add Ceylon Module", null, null, new AddModuleInputValidator(typeChecker));

                if (moduleName != null) {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            FileTemplateManager templateManager = FileTemplateManager.getInstance();
                            PsiDirectory subdirectory = DirectoryUtil.createSubdirectories(moduleName, eventPsiDir, ".");

                            Properties variables = new Properties();
                            String fullModuleName = (eventPackage.isEmpty() ? "" : eventPackage + ".") + moduleName;
                            variables.put("MODULE_NAME", fullModuleName);
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

    private static VirtualFile getSourceRoot(AnActionEvent e, VirtualFile eventDir) {
        return ProjectRootManager.getInstance(e.getProject()).getFileIndex().getSourceRootForFile(eventDir);
    }

    private static VirtualFile findEventDir(AnActionEvent e) {
        VirtualFile eventSourceFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        while (eventSourceFile != null && !(eventSourceFile instanceof VirtualDirectoryImpl)) {
            eventSourceFile = eventSourceFile.getParent();
        }

        return eventSourceFile;
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile eventDir = findEventDir(e);
        e.getPresentation().setVisible(eventDir != null && getSourceRoot(e, eventDir) != null);
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
                return "Please enter a name for your module";
            }
            if (!packageNameIsLegal(name)) {
                return String.format("\"%s\" is not a valid name for a module.", name);
            }
            if (moduleExists(name)) {
                return String.format("Module %s already exists.", name);
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
