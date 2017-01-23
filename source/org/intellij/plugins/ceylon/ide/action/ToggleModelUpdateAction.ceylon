import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    CommonDataKeys
}

import org.intellij.plugins.ceylon.ide.model {
    getModelManager
}

shared class ToggleModelUpdateAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists modelManager = getModelManager(project)) {
            modelManager.automaticModelUpdateEnabled
                    = !modelManager.automaticModelUpdateEnabled;
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists modelManager = getModelManager(project)) {

            e.presentation.enabled = true;

            value what = " automatic update of the Ceylon model (``modelManager.delayBeforeUpdatingAfterChange/1000``s after any change)";
            value action = modelManager.automaticModelUpdateEnabled then "Disable" else "Enable";
            e.presentation.text = action + what;
        } else {
            e.presentation.enabled = false;
        }
    }
}
