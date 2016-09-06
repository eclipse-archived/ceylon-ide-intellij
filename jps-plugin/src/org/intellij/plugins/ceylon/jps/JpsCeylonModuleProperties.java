package org.intellij.plugins.ceylon.jps;

public class JpsCeylonModuleProperties {

    private boolean compileForJvm = true;
    private boolean compileToJs;
    private boolean compileToDart;
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

    public boolean isCompileToDart() {
        return compileToDart;
    }

    public void setCompileToDart(boolean compileToDart) {
        this.compileToDart = compileToDart;
    }

    public String getSystemRepository() {
        return systemRepository;
    }

    public void setSystemRepository(String systemRepository) {
        this.systemRepository = systemRepository;
    }
}
