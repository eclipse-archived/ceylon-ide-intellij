package org.intellij.plugins.ceylon.ide.facet;

public class CeylonFacetState {

    private boolean compileForJvm;
    private boolean compileToJs;
    private boolean enableJavaCalling;
    private String systemRepository;

    public boolean isCompileForJvm() {
        return compileForJvm;
    }

    public void setCompileForJvm(boolean compileForJvm) {
        this.compileForJvm = compileForJvm;
    }

    public boolean isCompileToJs() {
        return compileToJs;
    }

    public void setCompileToJs(boolean compileToJs) {
        this.compileToJs = compileToJs;
    }

    public boolean isEnableJavaCalling() {
        return enableJavaCalling;
    }

    public void setEnableJavaCalling(boolean enableJavaCalling) {
        this.enableJavaCalling = enableJavaCalling;
    }

    public String getSystemRepository() {
        return systemRepository;
    }

    public void setSystemRepository(String systemRepository) {
        this.systemRepository = systemRepository;
    }
}
