import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    CommonDataKeys
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects,
    getModelManager
}

shared class UpdateModelAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project)) {

            if (exists man = getModelManager(project)) {
                man.scheduleModelUpdate(0, true);
            }
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project),
            !projects.ceylonProjects.empty) {

            e.presentation.enabled = true;
        } else {
            e.presentation.enabled = false;
        }
    }
}
