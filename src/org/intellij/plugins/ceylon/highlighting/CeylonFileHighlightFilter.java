package org.intellij.plugins.ceylon.highlighting;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.ceylon.CeylonFileType;

public class CeylonFileHighlightFilter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile virtualFile) {
        return virtualFile.getFileType() instanceof CeylonFileType;
    }
}
