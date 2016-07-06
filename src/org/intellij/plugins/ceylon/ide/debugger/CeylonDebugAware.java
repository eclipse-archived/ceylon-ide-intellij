package org.intellij.plugins.ceylon.ide.debugger;

import com.intellij.debugger.engine.JavaDebugAware;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;

public class CeylonDebugAware extends JavaDebugAware {
    @Override
    public boolean isBreakpointAware(PsiFile psiFile) {
        return psiFile.getFileType() == CeylonFileType.INSTANCE;
    }
}
