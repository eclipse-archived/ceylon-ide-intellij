import com.intellij.ide.util {
    DirectoryUtil {
        createSubdirectories
    }
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
    InputValidatorEx,
    Messages
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.pom {
    Navigatable
}
import com.intellij.psi {
    PsiDirectory,
    PsiManager
}

import java.lang {
    Runnable
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide {
    CeylonBundle {
        message
    }
}
import org.intellij.plugins.ceylon.ide.model {
    CeylonModelManager,
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.psi {
    ceylonFileFactory
}
import org.intellij.plugins.ceylon.ide.validate {
    NameValidator {
        packageNameIsLegal
    }
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class CeylonAddPackageAction() extends CeylonAddingFilesAction() {

    shared actual void createFiles(AnActionEvent e, IdeaCeylonProject project,
            VirtualFile srcRoot, String eventPackage, PsiDirectory eventPsiDir) {

        variable value madeTheChange = false;
        value modelManager = project.model.ideaProject.getComponent(`CeylonModelManager`);
        assert(exists p = e.project);

        try {
            if (exists modelManager) {
                modelManager.pauseAutomaticModelUpdate();
            }
            value packageName = Messages.showInputDialog(
                e.project,
                message("ceylon.package.wizard.message"),
                message("ceylon.package.wizard.title"),
                null,
                eventPackage,
                AddPackageInputValidator(),
                TextRange.from(eventPackage.size, 0)
            );

            if (exists packageName) {
                madeTheChange = true;
                ApplicationManager.application.runWriteAction(object satisfies Runnable {

                    shared actual void run() {
                        value srcRootDirectory = PsiManager.getInstance(p).findDirectory(srcRoot);
                        value subdirectory = createSubdirectories(packageName, srcRootDirectory, ".");
                        if (exists subdirectory) {
                            value pack = ceylonFileFactory.createPackageDescriptor(subdirectory,
                                packageName, true);

                            if (is Navigatable pack) {
                                pack.navigate(true);
                            }
                        } else {
                            Logger.getInstance(`CeylonAddModuleAction`)
                                .error("Can't create package descriptor: subdirectory is null.");
                        }
                    }
                });
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
        return icons.packageFolders;
    }

    class AddPackageInputValidator() satisfies InputValidatorEx {

        shared actual Boolean checkInput(String name) {
            return !name.trimmed.empty&&packageNameIsLegal(name);
        }

        shared actual Boolean canClose(String inputString) {
            return checkInput(inputString);
        }

        shared actual String? getErrorText(String name) {
            if (name.trimmed.empty) {
                return message("ceylon.package.wizard.error.blank");
            } else if (!packageNameIsLegal(name)) {
                return message("ceylon.package.wizard.error.illegal");
            }
            return null;
        }
    }
}
