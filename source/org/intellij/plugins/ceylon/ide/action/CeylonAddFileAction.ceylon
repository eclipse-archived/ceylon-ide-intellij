import com.intellij.ide.actions {
    CreateFileFromTemplateDialog,
    CreateFromTemplateAction
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    InputValidatorEx
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiDirectory,
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil {
        findChildOfType
    }
}

import java.lang {
    Types {
        nativeString
    }
}

import org.apache.commons.lang {
    StringUtils
}
import org.intellij.plugins.ceylon.ide {
    CeylonBundle {
        message
    }
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    ceylonFileFactory
}
import org.intellij.plugins.ceylon.ide.psi.impl {
    DeclarationPsiNameIdOwner
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class CeylonAddFileAction()
        extends CreateFromTemplateAction<PsiElement>("", message("ceylon.file.wizard.title"), icons.newFile) {

    String getCompleteFileName(String? input) {
        if (!exists input) {
            return "";
        }
        return if (input.endsWith(".ceylon")) then input else input + ".ceylon";
    }

    shared actual PsiElement createFile(String name, String templateName, PsiDirectory dir) {
        value unit = ceylonFileFactory.createUnit(dir, name, name + ".ceylon", templateName);

        if (is CeylonFile unit) {
            return findChildOfType(unit, `DeclarationPsiNameIdOwner`) else unit;
        }
        return unit;
    }

    shared actual void buildDialog(Project project, PsiDirectory directory,
            CreateFileFromTemplateDialog.Builder builder) {
        builder
            .setTitle(message("ceylon.file.wizard.title"))
            .addKind("File", icons.newFile, "unit")
            .addKind("Class", icons.classes, "class")
            .addKind("Function", icons.methods, "function")
            .addKind("Interface", icons.interfaces, "interface")
            .addKind("Object", icons.objects, "object")
            .setValidator(AddFileInputValidator(directory.virtualFile));
    }

    getActionName(PsiDirectory directory, String newName, String templateName)
            => "Creating Ceylon declaration " + newName;

    class AddFileInputValidator(VirtualFile directory) satisfies InputValidatorEx {

        shared actual Boolean checkInput(String inputString)
                => StringUtils.isNotBlank(inputString)
                    && nativeString(inputString).matches("(\\w|-)+")
                    && !directory.findChild(getCompleteFileName(inputString)) exists;

        shared actual String? getErrorText(String inputString) {
            value completeFileName = getCompleteFileName(inputString);
            if (StringUtils.isBlank(inputString)) {
                return message("ceylon.file.wizard.error.blank");
            } else if (!nativeString(inputString).matches("(\\w|-)+")) {
                return message("ceylon.file.wizard.error.illegal", inputString);
            } else {
                if (directory.findChild(completeFileName) exists) {
                    return message("ceylon.file.wizard.error.exists", completeFileName);
                }
            }
            return null;
        }

        canClose(String inputString)
                => checkInput(inputString);
    }
}
