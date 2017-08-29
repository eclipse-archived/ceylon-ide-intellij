package org.intellij.plugins.ceylon.ide.psi;

import com.intellij.psi.tree.IElementType;

public class TokenUtil {
    public static IElementType fromInt(int value) {
        return TokenTypes.fromInt(value);
    }
}
