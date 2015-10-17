package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CeylonSettings",
        storages = {@Storage(file = StoragePathMacros.APP_CONFIG + "/ceylon.xml")}
)
public class CeylonSettings implements PersistentStateComponent<CeylonSettings.CeylonOptions> {

    private CeylonOptions myOptions = new CeylonOptions();

    @Nullable
    @Override
    public CeylonOptions getState() {
        return myOptions;
    }

    @Override
    public void loadState(CeylonOptions state) {
        myOptions = state;
    }

    static class CeylonOptions {
        public String defaultTargetVm = "jvm";
        public String defaultSourceFolder = "source";
        public String defaultResourceFolder = "resource";
    }

    public static CeylonSettings getInstance() {
        if (ApplicationManager.getApplication().isDisposed()) {
            return new CeylonSettings();
        }
        else {
            return ServiceManager.getService(CeylonSettings.class);
        }
    }

    public String getDefaultTargetVm() {
        return myOptions.defaultTargetVm;
    }

    public String getDefaultSourceFolder() {
        return myOptions.defaultSourceFolder;
    }

    public String getDefaultResourceFolder() {
        return myOptions.defaultResourceFolder;
    }

    public void setDefaultTargetVm(String defaultTargetVm) {
        myOptions.defaultTargetVm = defaultTargetVm;
    }

    public void setDefaultSourceFolder(String defaultSourceFolder) {
        myOptions.defaultSourceFolder = defaultSourceFolder;
    }

    public void setDefaultResourceFolder(String defaultResourceFolder) {
        myOptions.defaultResourceFolder = defaultResourceFolder;
    }
}
