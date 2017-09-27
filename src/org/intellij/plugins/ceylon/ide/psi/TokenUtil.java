package org.eclipse.ceylon.ide.intellij.psi;

import com.intellij.psi.tree.IElementType;

public class TokenUtil {
    public static IElementType fromInt(int value) {
        return TokenTypes.fromInt(value);
    }
}
