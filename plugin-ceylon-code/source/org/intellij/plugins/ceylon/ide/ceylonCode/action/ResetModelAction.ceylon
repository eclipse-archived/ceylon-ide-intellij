import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    PlatformDataKeys
}
import ceylon.interop.java {
    javaClass
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    CeylonModelManager
}

shared class ResetModelAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project
                = PlatformDataKeys.project.getData(e.dataContext),
            exists projects
                = project.getComponent(javaClass<IdeaCeylonProjects>())) {

            for (p in projects.ceylonProjects) {
                p.build.requestFullBuild();
                p.build.classPathChanged();
            }

            if (exists man
                    = project.getComponent(javaClass<CeylonModelManager>())) {
                man.scheduleModelUpdate(0, true);
            }
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project
                = PlatformDataKeys.project.getData(e.dataContext),
            exists projects
                = project.getComponent(javaClass<IdeaCeylonProjects>()),
            !projects.ceylonProjects.empty) {

            e.presentation.enabled = true;
        } else {
            e.presentation.enabled = false;
        }
    }
}
