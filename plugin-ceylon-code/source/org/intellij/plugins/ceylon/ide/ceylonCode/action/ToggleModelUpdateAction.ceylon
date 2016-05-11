import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}

shared class ToggleModelUpdateAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = PlatformDataKeys.\iPROJECT.getData(e.dataContext),
            exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {

            // TODO togle automatic update of model
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = PlatformDataKeys.\iPROJECT.getData(e.dataContext),
            exists projects = project.getComponent(javaClass<IdeaCeylonProjects>())) {

            e.presentation.enabled = true;

            if (true) { // TODO check if auto update is enabled
                e.presentation.text = "Disable automatic update of model";
            } else {
                e.presentation.text = "Enable automatic update of model";
            }
        } else {
            e.presentation.enabled = false;
        }
    }
}
