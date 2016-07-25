import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.components {
    ...
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

state__TYPE {
    name = "CeylonCompletionSettings";
    storages = {
        storage {
            file = "$APP_CONFIG$/ceylon.xml";
        }
    };
}
shared class CompletionSettings() satisfies PersistentStateComponent<CompletionOptions> {

    shared actual CompletionOptions state {
        return completionOptions;
    }

    shared actual void loadState(CompletionOptions state) {
        completionOptions = state;
    }

    shared CompletionOptions options {
        return completionOptions;
    }
}

shared CompletionSettings completionSettings {
    if (ApplicationManager.application.isDisposed()) {
        return CompletionSettings();
    } else {
        return ServiceManager.getService(javaClass<CompletionSettings>());
    }
}