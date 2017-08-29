import com.intellij.openapi.ui {
    DialogWrapper,
    ValidationInfo
}

import java.io {
    File
}

import org.intellij.plugins.ceylon.ide {
    CeylonBundle
}
import java.awt {
    Component
}

shared class AetherRepositoryDialog(Component parent)
        extends DialogWrapper(parent, false) {

    value form = AetherRepositoryForm();

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
