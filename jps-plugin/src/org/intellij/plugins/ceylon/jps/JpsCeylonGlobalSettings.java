package org.intellij.plugins.ceylon.jps;

import org.jetbrains.annotations.Nullable;

class JpsCeylonGlobalSettings {

    @Nullable
    public static JpsCeylonGlobalSettings INSTANCE;

    private boolean useOutProcessBuild = true;

    public boolean isUseOutProcessBuild() {
        return useOutProcessBuild;
    }

    public void setUseOutProcessBuild(boolean useOutProcessBuild) {
        this.useOutProcessBuild = useOutProcessBuild;
    }
}
