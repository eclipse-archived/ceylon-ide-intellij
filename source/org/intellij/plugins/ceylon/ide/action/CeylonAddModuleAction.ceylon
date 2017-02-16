import com.intellij.ide.projectView.impl {
    ProjectRootsUtil
}
import com.intellij.ide.util {
    DirectoryUtil
}
import com.intellij.openapi.actionSystem {
    AnActionEvent
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.ui {
    DialogWrapper
}
import com.intellij.openapi.util.text {
    StringUtil
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiDirectory,
    PsiFile,
    PsiManager
}
import com.redhat.ceylon.common {
    Backend
}

import java.lang {
    Runnable
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.model {
    CeylonModelManager,
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.psi {
    ceylonFileFactory
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}
import org.intellij.plugins.ceylon.ide.wizard {
    CreateCeylonModuleWizard
}

shared class CeylonAddModuleAction() extends CeylonAddingFilesAction() {

    shared actual void createFiles(AnActionEvent e, IdeaCeylonProject project,
            VirtualFile srcRoot, String eventPackage, PsiDirectory srcRootDir) {

        value modelManager = project.model.ideaProject.getComponent(`CeylonModelManager`);
        variable value madeTheChange = true;
        value p = e.project;
        if (!exists p) {
            return;
        }

        try {
            value wizard = CreateCeylonModuleWizard(p, project);
            if (exists modelManager) {
                modelManager.pauseAutomaticModelUpdate();
            }
            wizard.show();

            if (wizard.exitCode == DialogWrapper.okExitCode) {
                value moduleName = wizard.moduleName;
                value hasSuffix = wizard.compilationUnitName.endsWith(".ceylon");
                value unitFileName = hasSuffix then wizard.compilationUnitName else wizard.compilationUnitName + ".ceylon";
                value unitName = hasSuffix then wizard.compilationUnitName.substring(0, ".ceylon".size) else wizard.compilationUnitName;

                ApplicationManager.application.runWriteAction(object satisfies Runnable {

                    shared actual void run() {
                        configureSourceRootIfNeeded();
                        value subdirectory = findOrCreateModuleDirectory();
                        assert(exists subdirectory);

                        try {
                            variable Backend? backend;
                            if (project.compileToJs, !project.compileToJava) {
                                backend = Backend.\iJavaScript;
                            } else if (!project.compileToJs, project.compileToJava) {
                                backend = Backend.\iJava;
                            } else {
                                backend = null;
                            }
                            ceylonFileFactory.createModuleDescriptor(subdirectory, moduleName,
                                wizard.moduleVersion, backend);
                            ceylonFileFactory.createPackageDescriptor(subdirectory, moduleName);
                            if (StringUtil.isNotEmpty(unitName)) {
                                value run = ceylonFileFactory.createRun(subdirectory, moduleName,
                                    unitFileName, unitName);
                                if (is PsiFile run) {
                                    run.navigate(true);
                                }
                            }
                        }
                        catch (e1) {
                            Logger.getInstance(`CeylonAddModuleAction`)
                                .error("Can't create file from template", e1);
                        }
                    }

                    void configureSourceRootIfNeeded() {
                        if (ProjectRootsUtil.isSourceRoot(srcRootDir),
                            !project.configuration.sourceDirectories.contains(srcRootDir.virtualFile)) {

                            project.addSourceRoot(srcRootDir.virtualFile);
                            project.configuration.save();
                        }
                    }

                    PsiDirectory? findOrCreateModuleDirectory() {
                        value targetDir = srcRoot.findChild(moduleName.replace(".", "/"));
                        return if (exists targetDir)
                        then PsiManager.getInstance(p).findDirectory(targetDir)
                        else DirectoryUtil.createSubdirectories(moduleName, srcRootDir, ".");
                    }
                });
                madeTheChange = true;
            } else {
                madeTheChange = false;
            }
        }
        finally {
            if (exists modelManager) {
                if (madeTheChange) {
                    modelManager.resumeAutomaticModelUpdate(0);
                } else {
                    modelManager.resumeAutomaticModelUpdate();
                }
            }
        }
    }

    shared actual Icon ceylonIcon {
        return icons.moduleFolders;
    }
}
