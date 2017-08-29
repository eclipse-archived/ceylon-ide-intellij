import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.components {
    state,
    storage,
    PersistentStateComponent,
    ServiceManager
}

shared CeylonSettings ceylonSettings
        => if (ApplicationManager.application.isDisposed())
        then CeylonSettings()
        else ServiceManager.getService(`CeylonSettings`);

shared class CeylonOptions() {
    shared variable String defaultTargetVm = "jvm";
    shared variable String defaultSourceFolder = "source";
    shared variable String defaultResourceFolder = "resource";
    shared variable Boolean useOutProcessBuild = true;
    shared variable Boolean makeCompilerVerbose = false;
    shared variable String verbosityLevel = "";
    shared variable Integer autoUpdateInterval = 4000;
    shared variable Integer modelUpdateTimeoutMinutes = 6;
    shared variable Boolean lowerModelUpdatePriority = false;
    shared variable Boolean highlightedLabels = true;
}

state {
    name = "CeylonSettings";
    storages = { storage {file = "$APP_CONFIG$/ceylon.xml";} };
}
shared class CeylonSettings
        satisfies PersistentStateComponent<CeylonOptions> {

    shared new() {}

    variable CeylonOptions myOptions = CeylonOptions();

    state => myOptions;

    loadState(CeylonOptions state) => myOptions = state;

    shared String defaultTargetVm => myOptions.defaultTargetVm;

    shared String defaultSourceFolder => myOptions.defaultSourceFolder;

    shared String defaultResourceFolder => myOptions.defaultResourceFolder;

    shared Boolean useOutProcessBuild => myOptions.useOutProcessBuild;

    shared Boolean compilerVerbose => myOptions.makeCompilerVerbose;

    shared String verbosityLevel => myOptions.verbosityLevel;

    shared Integer autoUpdateInterval => myOptions.autoUpdateInterval;

    shared Integer modelUpdateTimeoutMinutes => myOptions.modelUpdateTimeoutMinutes;

    shared Boolean lowerModelUpdatePriority => myOptions.lowerModelUpdatePriority;

    shared Boolean highlightedLabels => myOptions.highlightedLabels;

    assign defaultTargetVm
            => myOptions.defaultTargetVm = defaultTargetVm;

    assign defaultSourceFolder
            => myOptions.defaultSourceFolder = defaultSourceFolder;

    assign defaultResourceFolder
            => myOptions.defaultResourceFolder = defaultResourceFolder;

    assign useOutProcessBuild
            => myOptions.useOutProcessBuild = useOutProcessBuild;

    assign compilerVerbose
            => myOptions.makeCompilerVerbose = compilerVerbose;

    assign verbosityLevel
            => myOptions.verbosityLevel = verbosityLevel;

    assign autoUpdateInterval
            => myOptions.autoUpdateInterval = autoUpdateInterval;

    assign modelUpdateTimeoutMinutes
            => myOptions.modelUpdateTimeoutMinutes = modelUpdateTimeoutMinutes;

    assign lowerModelUpdatePriority
            => myOptions.lowerModelUpdatePriority = lowerModelUpdatePriority;

    assign highlightedLabels
            => myOptions.highlightedLabels = highlightedLabels;
}
