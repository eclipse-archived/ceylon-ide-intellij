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
        if (exists project = PlatformDataKeys.\iPROJECT.getData(e.dataContext),
            exists modelManager = project.getComponent(javaClass<CeylonModelManager>())) {
            modelManager.periodicTypecheckingEnabled = ! modelManager.periodicTypecheckingEnabled;
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = PlatformDataKeys.\iPROJECT.getData(e.dataContext),
            exists modelManager = project.getComponent(javaClass<CeylonModelManager>())) {

            e.presentation.enabled = true;

            if (modelManager.periodicTypecheckingEnabled) {
                e.presentation.text = "Disable automatic update of model";
            } else {
                e.presentation.text = "Enable automatic update of model";
            }
        } else {
            e.presentation.enabled = false;
        }
    }
}
