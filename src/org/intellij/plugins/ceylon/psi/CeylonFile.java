package org.intellij.plugins.ceylon.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

public class CeylonFile extends PsiFileBase {

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }
}
