import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    ValidationInfo
}
import com.intellij.ui.components {
    JBTextField
}

import java.awt {
    ...
}

import javax.swing {
    ...
}

import org.apache.commons.lang {
    StringUtils
}
import org.intellij.plugins.ceylon.ide {
    CeylonBundle {
        message
    }
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.validate {
    NameValidator {
        packageNameIsLegal,
        unitNameIsLegal
    }
}

shared class CeylonCreateCeylonModuleWizard(Project project, Object ceylonProject)
        extends CreateCeylonModuleWizard(project) {

    title = message("ceylon.module.wizard.title");
    init();

    preferredFocusedComponent => moduleName;

    shared actual ValidationInfo? doValidate() {
        value name = moduleName.text;
        if (StringUtils.isBlank(name)) {
            return ValidationInfo(message("ceylon.module.wizard.error.blank"), moduleName);
        } else if (!packageNameIsLegal(name)) {
            return ValidationInfo(message("ceylon.module.wizard.error.illegal", name), moduleName);
        } else if (moduleExists(name)) {
            return ValidationInfo(message("ceylon.module.wizard.error.duplicate", name), moduleName);
        }
        value unit = compilationUnit.text;
        if (!unitNameIsLegal(unit)) {
            return ValidationInfo(message("ceylon.module.wizard.error.unit"), compilationUnit);
        }
        return null;
    }

    Boolean moduleExists(String moduleName) {
        if (is IdeaCeylonProject project,
            exists projectModules = project.modules) {
            value modules = projectModules.typecheckerModules;
            for (mod in modules.listOfModules) {
                if (mod.nameAsString == moduleName) {
                    return true;
                }
            }
        }
        return false;
    }

    createCenterPanel() => contentPane;

    shared String moduleNameText => moduleName.text;

    shared String moduleVersionText => moduleVersion.text;

    shared String compilationUnitNameText => compilationUnit.text;

    shared actual void createUIComponents() {
        value compilationUnit = JBTextField();
        compilationUnit.emptyText.setText("No runnable compilation unit");
        super.compilationUnit = compilationUnit;
    }

}
