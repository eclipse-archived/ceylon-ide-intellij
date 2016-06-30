package org.intellij.plugins.ceylon.ide.ceylonCode.settings;

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

    @SuppressWarnings("WeakerAccess")
    public static class CeylonOptions {
        public String defaultTargetVm = "jvm";
        public String defaultSourceFolder = "source";
        public String defaultResourceFolder = "resource";
        public boolean useOutProcessBuild = true;
        public boolean makeCompilerVerbose = false;
        public String verbosityLevel = "";
//        public boolean autoUpdateModel = true;
        public int autoUpdateInterval = 4000;
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

    public boolean isUseOutProcessBuild() {
        return myOptions.useOutProcessBuild;
    }

    public boolean isCompilerVerbose() {
        return myOptions.makeCompilerVerbose;
    }

    public String getVerbosityLevel() {
        return myOptions.verbosityLevel;
    }

//    public boolean isAutoUpdateModel() {
//        return myOptions.autoUpdateModel;
//    }

    public int getAutoUpdateInterval() {
        return myOptions.autoUpdateInterval;
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

    public void setUseOutProcessBuild(boolean useOutProcessBuild) {
        myOptions.useOutProcessBuild = useOutProcessBuild;
    }

    public void setCompilerVerbose(boolean verbose) {
        myOptions.makeCompilerVerbose = verbose;
    }

    public void setVerbosityLevel(String verbosityLevel) {
        myOptions.verbosityLevel = verbosityLevel;
    }

//    public void setAutoUpdateModel(boolean autoUpdateModel) {
//        myOptions.autoUpdateModel = autoUpdateModel;
//    }

    public void setAutoUpdateInterval(int autoUpdateInterval) {
        myOptions.autoUpdateInterval = autoUpdateInterval;
    }
}
