package org.intellij.plugins.ceylon.ide.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;

public class CeylonTokenType extends IElementType {

    public CeylonTokenType(@NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }
}
