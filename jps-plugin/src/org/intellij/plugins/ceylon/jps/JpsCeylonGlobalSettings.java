package org.intellij.plugins.ceylon.jps;

import org.jetbrains.annotations.Nullable;

class JpsCeylonGlobalSettings {

    @Nullable
    public static JpsCeylonGlobalSettings INSTANCE;

    private boolean useOutProcessBuild = true;
    private boolean makeCompilerVerbose = false;
    private String verbosityLevel = "";

    public boolean isUseOutProcessBuild() {
        return useOutProcessBuild;
    }

    public void setUseOutProcessBuild(boolean useOutProcessBuild) {
        this.useOutProcessBuild = useOutProcessBuild;
    }

    public boolean isMakeCompilerVerbose() {
        return makeCompilerVerbose;
    }

    public void setMakeCompilerVerbose(boolean makeCompilerVerbose) {
        this.makeCompilerVerbose = makeCompilerVerbose;
    }

    public String getVerbosityLevel() {
        return verbosityLevel;
    }

    public void setVerbosityLevel(String verbosityLevel) {
        this.verbosityLevel = verbosityLevel;
    }
}
