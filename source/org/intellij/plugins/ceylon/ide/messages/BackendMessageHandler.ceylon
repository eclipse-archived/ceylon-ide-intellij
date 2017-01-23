import com.intellij.compiler.server {
    CustomBuilderMessageHandler,
    BuildManagerListener
}
import com.intellij.execution.impl {
    ConsoleViewImpl
}
import com.intellij.execution.ui {
    ConsoleViewContentType
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.wm {
    ToolWindowManager {
        toolWindows=getInstance
    },
    ToolWindowId {
        messagesWindow
    }
}

import java.util {
    UUID
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter,
    ceylonHighlightingColors
}

shared class BackendMessageHandler(Project project)
        satisfies CustomBuilderMessageHandler
                & BuildManagerListener {

    value console = ConsoleViewImpl(project, true);
    value fileAttrs = ConsoleViewContentType("CEYLON_FILE", ceylonHighlightingColors.identifier);
    value errorAttrs = ConsoleViewContentType("CEYLON_ERROR", CodeInsightColors.wrongReferencesAttributes);
    value warningAttrs = ConsoleViewContentType("CEYLON_ERROR", CodeInsightColors.warningsAttributes);

    beforeBuildProcessStarted(Project? project, UUID? sessionId)
            => noop();

    buildFinished(Project? project, UUID? sessionId, Boolean isAutomake)
            => noop();

    "Clears the Ceylon Build output"
    shared actual void buildStarted(Project? project, UUID? sessionId, Boolean isAutomake)
            => console.clear();

    "Concatenates the message to the Ceylon Build output"
    shared actual void messageReceived(String builderId, String messageType, String messageText) {
        if (builderId == "ceylon") {
            value messages = toolWindows(project).getToolWindow(messagesWindow);
            ApplicationManager.application.invokeLater(() {
                if (exists messages,
                    !exists _ = messages.contentManager.getContent(console.component)) {
                    messages.contentManager.addContent(
                        messages.contentManager.factory.createContent(console.component, "Ceylon Build", true)
                    );
                }
            });

            value parts = messageText.split('✝'.equals).sequence();

            function color(String str) =>
                highlighter.parseQuotedMessage(str, project, false, (what, how) {
                    value contentType = if (exists how)
                        then ConsoleViewContentType("", how)
                        else ConsoleViewContentType.normalOutput;
                    console.print(what, contentType);
                });

            function part(Integer index)
                    => parts[index] else "";

            value attrs = switch(messageType)
            case ("error") errorAttrs
            case ("warning") warningAttrs
            else ConsoleViewContentType.normalOutput;

            if (parts.size == 3) {
                console.print(part(0), ConsoleViewContentType.normalOutput);
                console.print(part(1), attrs);
                color(part(2));
            }
            else {
                console.print(part(0), fileAttrs);
                console.print(part(1), ConsoleViewContentType.normalOutput);
                console.print(part(2), attrs);
                color(part(3));
            }

            console.print("\n", ConsoleViewContentType.normalOutput);
        }
    }
}
