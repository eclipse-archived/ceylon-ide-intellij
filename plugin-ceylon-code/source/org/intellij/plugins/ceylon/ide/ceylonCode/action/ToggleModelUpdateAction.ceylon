import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    CeylonModelManager
}

shared class ToggleModelUpdateAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project
                = PlatformDataKeys.project.getData(e.dataContext),
            exists modelManager
                = project.getComponent(javaClass<CeylonModelManager>())) {
            modelManager.automaticModelUpdateEnabled
                    = !modelManager.automaticModelUpdateEnabled;
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project
                = PlatformDataKeys.project.getData(e.dataContext),
            exists modelManager
                = project.getComponent(javaClass<CeylonModelManager>())) {

            e.presentation.enabled = true;

            value what = " automatic update of the Ceylon model (``modelManager.delayBeforeUpdatingAfterChange/1000`` seconds after any change)";
            value action = modelManager.automaticModelUpdateEnabled then "Disable" else "Enable";
            e.presentation.setText(action + what);
        } else {
            e.presentation.enabled = false;
        }
    }
}
