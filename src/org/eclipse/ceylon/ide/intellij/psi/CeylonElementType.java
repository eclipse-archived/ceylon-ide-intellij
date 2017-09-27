package org.eclipse.ceylon.ide.intellij.psi;

import com.intellij.psi.tree.IElementType;
import org.eclipse.ceylon.ide.intellij.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;

public class CeylonElementType extends IElementType {

    public CeylonElementType(@NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }
}