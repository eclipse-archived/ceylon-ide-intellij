package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;

public class CeylonTokenType extends IElementType {

    public CeylonTokenType(@NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }
}
