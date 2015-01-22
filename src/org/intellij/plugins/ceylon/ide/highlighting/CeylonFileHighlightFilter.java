package org.intellij.plugins.ceylon.ide.highlighting;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.ceylon.ide.CeylonFileType;

/**
 * Underlines a Ceylon file name when it contains errors.
 */
public class CeylonFileHighlightFilter implements Condition<VirtualFile> {
    @Override
    public boolean value(VirtualFile virtualFile) {
        return virtualFile.getFileType() instanceof CeylonFileType;
    }
}
