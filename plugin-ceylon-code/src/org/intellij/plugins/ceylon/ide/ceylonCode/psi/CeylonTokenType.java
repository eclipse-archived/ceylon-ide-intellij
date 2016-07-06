package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.jetbrains.annotations.NonNls;

public class CeylonTokenType extends IElementType {

    public CeylonTokenType(@NonNls String debugName) {
        super(debugName, (Language) ceylonLanguage_.get_());
    }
}
