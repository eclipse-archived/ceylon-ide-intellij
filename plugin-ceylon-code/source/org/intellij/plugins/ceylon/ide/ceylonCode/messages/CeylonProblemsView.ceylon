import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass,
    JavaRunnable
}

import com.intellij.ide.impl {
    ContentManagerWatcher
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.components {
    ServiceManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.startup {
    StartupManager
}
import com.intellij.openapi.wm {
    ToolWindowManager,
    ToolWindow,
    ToolWindowAnchor
}
import com.intellij.ui.content {
    ContentManager
}
import com.redhat.ceylon.ide.common.model {
    Severity
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

String ceylonProblemsContentId = "";
String toolWindowId = "Ceylon Problems";

shared CeylonProblemsView getCeylonProblemsView(Project project)
        => ServiceManager.getService(project, javaClass<CeylonProblemsView>());

shared interface CeylonProblemsView {
    shared formal ContentManager contentManager;

    shared formal void updateMessages(IdeaCeylonProject project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages);
}

shared class CeylonProblemsViewImpl(project, startupManager, toolWindowManager)
        satisfies CeylonProblemsView {

    ToolWindowManager toolWindowManager;
    StartupManager startupManager;
    Project project;
    late ToolWindow myToolWindow;
    variable value initialized = false;
    value myPostponedRunnables = ArrayList<Anything()>();

    value runnable = JavaRunnable(() {
        myToolWindow = toolWindowManager.registerToolWindow(
            toolWindowId,
            false,
            ToolWindowAnchor.\iBOTTOM,
            project,
            false
        );
        myToolWindow.title = toolWindowId;
        initialized = true;

        value list = CeylonProblemsList(project);
        list.setup();
        value content = myToolWindow.contentManager.factory
            .createContent(list, ceylonProblemsContentId, false);
        myToolWindow.contentManager.addContent(content);

        myToolWindow.icon = ideaIcons.problemsViewOk;
        ContentManagerWatcher(myToolWindow, myToolWindow.contentManager);

        myPostponedRunnables.each((func) => func());
        myPostponedRunnables.clear();

        myToolWindow.hide(null);
    });

    if (project.initialized) {
        ApplicationManager.application.invokeLater(runnable);
    } else {
        startupManager.registerPostStartupActivity(runnable);
    }

    shared actual ContentManager contentManager => myToolWindow.contentManager;

    shared actual void updateMessages(IdeaCeylonProject project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages) {

        void func() {
            value content = contentManager.contents.get(0);
            if (is CeylonProblemsList list = content.component) {
                list.updateErrors(project, frontendMessages, backendMessages, projectMessages);
            }

            if (project.build.messages.empty) {
                myToolWindow.icon = ideaIcons.problemsViewOk;
            } else if (project.build.messages.find((m) => m.severity == Severity.error) exists) {
                myToolWindow.icon = ideaIcons.problemsViewErrors;
            } else {
                myToolWindow.icon = ideaIcons.problemsViewWarnings;
            }
        }

        if (initialized) {
            ApplicationManager.application.invokeLater(JavaRunnable(func));
        } else {
            myPostponedRunnables.add(func);
        }
    }
}
