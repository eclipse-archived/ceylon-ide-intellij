import com.intellij.openapi.ui {
    DialogWrapper,
    ValidationInfo
}

import java.awt {
    Component
}
import java.io {
    File
}

import org.intellij.plugins.ceylon.ide {
    CeylonBundle
}

shared class AetherRepositoryDialog(Component parent)
        extends DialogWrapper(parent, false) {

    value form = CeylonAetherRepositoryForm();

    init();
    title = CeylonBundle.message("project.wizard.repo.maven.title");

    createCenterPanel() => form.mainPanel;

    preferredFocusedComponent => form.repoField;

    shared String repository => form.repoField.text;

    shared actual ValidationInfo? doValidate() {
        if (!repository.empty
            && !File(repository).\iexists()) {
            return ValidationInfo(CeylonBundle.message("project.wizard.repo.maven.invalidpath"), form.repoField);
        }
        return null;
    }

}
