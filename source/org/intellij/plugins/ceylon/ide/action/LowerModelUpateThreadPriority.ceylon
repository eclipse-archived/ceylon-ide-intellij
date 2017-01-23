import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent
}

import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}

shared class LowerModelUpdatePriorityAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        ceylonSettings.lowerModelUpdatePriority
        = !ceylonSettings.lowerModelUpdatePriority;
    }

    shared actual void update(AnActionEvent e) {
        e.presentation.enabled = true;

        e.presentation.text =
        if (ceylonSettings.lowerModelUpdatePriority)
        then "Restore the processing priority of Ceylon model updates to the defaut priority"
        else "Decrease the processing priority of Ceylon model updates";
    }
}
