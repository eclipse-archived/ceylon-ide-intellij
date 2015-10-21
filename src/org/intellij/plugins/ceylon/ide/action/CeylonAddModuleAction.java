package org.intellij.plugins.ceylon.ide.action;

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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.intellij.plugins.ceylon.ide.wizard.CreateCeylonModuleWizard;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Properties;

public class CeylonAddModuleAction extends CeylonAddingFilesAction {

    public CeylonAddModuleAction() {
        super(ideaIcons_.get_().getModules());
    }

    @Override
    protected void createFiles(final AnActionEvent e, final TypeChecker typeChecker, final VirtualFile srcRoot, final String eventPackage, final PsiDirectory srcRootDir) {
        final CreateCeylonModuleWizard wizard = new CreateCeylonModuleWizard(e.getProject(), typeChecker);

        wizard.show();

        if (wizard.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            final String moduleName = wizard.getModuleName();
            final String unitFileName = wizard.getCompilationUnitName().endsWith(".ceylon") ? wizard.getCompilationUnitName() : wizard.getCompilationUnitName() + ".ceylon";
            final String unitName = wizard.getCompilationUnitName().endsWith(".ceylon") ? wizard.getCompilationUnitName().substring(0, ".ceylon".length()) : wizard.getCompilationUnitName();

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    FileTemplateManager templateManager = FileTemplateManager.getInstance(e.getProject());
                    PsiDirectory subdirectory = findOrCreateModuleDirectory();

                    Properties variables = new Properties();
                    variables.put("MODULE_NAME", moduleName);
                    variables.put("MODULE_VERSION", wizard.getModuleVersion());
                    variables.put("IS_SHARED", Boolean.toString(wizard.isSharedPackage()));
                    variables.put("FUN_NAME", unitName);

                    try {
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("module.ceylon"), "module.ceylon", variables, subdirectory);
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("package.ceylon"), "package.ceylon", variables, subdirectory);
                        FileTemplateUtil.createFromTemplate(templateManager.getInternalTemplate("run.ceylon"), unitFileName, variables, subdirectory);
                    } catch (Exception e1) {
                        Logger.getInstance(CeylonAddModuleAction.class).error("Can't create file from template", e1);
                    }

                    // FIXME com.redhat.ceylon.compiler.typechecker.context.PhasedUnits expects to parse modules from the root folder
                    // so the only way to not parse everything here seems to be by modifying PhaseUnits or injecting a different ModuleManager into it
                    FileSystemVirtualFile virtualFile = new FileSystemVirtualFile(new File(srcRoot.getCanonicalPath()));
                    parseUnit(virtualFile, typeChecker, e.getProject());
                }

                @NotNull
                private PsiDirectory findOrCreateModuleDirectory() {
                    VirtualFile targetDir = srcRoot.findChild(moduleName.replace('.', '/'));
                    return targetDir != null ? PsiManager.getInstance(e.getProject()).findDirectory(targetDir) : DirectoryUtil.createSubdirectories(moduleName, srcRootDir, ".");
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
}
