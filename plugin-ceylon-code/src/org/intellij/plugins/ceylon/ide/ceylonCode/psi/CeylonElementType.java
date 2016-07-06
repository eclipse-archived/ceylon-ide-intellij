package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.jetbrains.annotations.NonNls;

public class CeylonElementType extends IElementType {

    public CeylonElementType(@NonNls String debugName) {
        super(debugName, ceylonLanguage_.get_());
    }
}