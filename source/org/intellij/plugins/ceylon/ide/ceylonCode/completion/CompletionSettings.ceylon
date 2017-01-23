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

    state => completionOptions;

    loadState(CompletionOptions state) => completionOptions = state;

    shared CompletionOptions options => completionOptions;
}

shared CompletionSettings completionSettings
        => ApplicationManager.application.isDisposed()
        then CompletionSettings()
        else ServiceManager.getService(`CompletionSettings`);