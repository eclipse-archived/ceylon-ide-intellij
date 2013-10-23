package org.intellij.plugins.ceylon.lang;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.intellij.plugins.ceylon.psi.TokenTypes;

public class CeylonQuoteHandler extends SimpleTokenSetQuoteHandler {

    public CeylonQuoteHandler() {
        super(TokenTypes.STRING_LITERAL.getTokenType(), TokenTypes.CHAR_LITERAL.getTokenType());
    }
}
