import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    LangDataKeys
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class ConfigureCeylonAction() extends AnAction(icons.ceylon) {
    shared actual void actionPerformed(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.moduleContext)) {
            AndroidStudioSupportImpl().setupModule(mod);
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.moduleContext)) {
            e.presentation.enabledAndVisible = true;
        } else {
            e.presentation.enabledAndVisible = false;
        }
    }
}
