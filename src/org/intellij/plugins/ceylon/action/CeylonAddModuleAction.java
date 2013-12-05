package org.intellij.plugins.ceylon.action;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
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
import org.intellij.plugins.ceylon.annotator.TypeCheckerManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

public class CeylonAddModuleAction extends AnAction {

    public CeylonAddModuleAction() {
        super(PlatformIcons.PACKAGE_ICON);
    }

    public void actionPerformed(final AnActionEvent e) {
        final VirtualFile eventDir = findEventDir(e);

        if (eventDir != null && e.getProject() != null) {
            TypeCheckerManager manager = ServiceManager.getService(e.getProject(), TypeCheckerManager.class);
            final TypeChecker typeChecker = manager.getTypeChecker();

            VirtualFile srcRoot = getSourceRoot(e, eventDir);
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

                            // TODO if parent folder is "source", we basically parse eveything again... is this bad?
                            typeChecker.getPhasedUnits().parseUnit(new FileSystemVirtualFile(new File(subdirectory.getVirtualFile().getParent().getCanonicalPath())));
                        }
                    });
                }
            }
        }
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
        final Pattern pattern = Pattern.compile("([a-z_A-Z]|\\\\[iI])[a-z_A-Z0-9]*(\\.([a-z_A-Z]|\\\\[iI])[a-z_A-Z0-9]*)*");
        private TypeChecker typeChecker;

        public AddModuleInputValidator(TypeChecker typeChecker) {

            this.typeChecker = typeChecker;
        }

        @Override
        public boolean checkInput(String name) {
            return pattern.matcher(name).matches() && !moduleExists(name);
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String name) {
            if (!pattern.matcher(name).matches()) {
                return String.format("\"%s\" is not a valid name for a module.", name);
            } else if (moduleExists(name)) {
                return String.format("Module %s already exists.", name);
            }
            return null;
        }

        private boolean moduleExists(String moduleName) {
            for (Module module : typeChecker.getContext().getModules().getListOfModules()) {
                if (module.getNameAsString().equals(moduleName)) {
                    return true;
                }
            }

            return false;
        }
    }
}
