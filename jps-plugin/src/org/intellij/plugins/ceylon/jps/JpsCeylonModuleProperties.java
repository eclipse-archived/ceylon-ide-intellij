package org.intellij.plugins.ceylon.jps;

public class JpsCeylonModuleProperties {

    private boolean compileForJvm = true;
    private boolean compileToJs;
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

    public String getSystemRepository() {
        return systemRepository;
    }

    public void setSystemRepository(String systemRepository) {
        this.systemRepository = systemRepository;
    }
}
