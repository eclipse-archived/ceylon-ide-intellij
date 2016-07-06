package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.completionOptions_;

import com.redhat.ceylon.ide.common.settings.CompletionOptions;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CeylonCompletionSettings",
        storages = {@Storage(file = StoragePathMacros.APP_CONFIG + "/ceylon.xml")}
)
public class CompletionSettings implements PersistentStateComponent<CompletionOptions> {

    @Nullable
    @Override
    public CompletionOptions getState() {
        return completionOptions_.get_();
    }

    @Override
    public void loadState(CompletionOptions state) {
        completionOptions_.set_(state);
    }

    public static CompletionSettings getInstance() {
        if (ApplicationManager.getApplication().isDisposed()) {
            return new CompletionSettings();
        }
        else {
            return ServiceManager.getService(CompletionSettings.class);
        }
    }

    public CompletionOptions getOptions() {
        return completionOptions_.get_();
    }
}
