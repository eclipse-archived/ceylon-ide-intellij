package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.intellij.plugins.ceylon.ide.psi.TokenTypes;

public class CeylonQuoteHandler extends SimpleTokenSetQuoteHandler {

    public CeylonQuoteHandler() {
        super(
                TokenTypes.STRING_LITERAL.getTokenType(),
                TokenTypes.STRING_START.getTokenType(),
                TokenTypes.STRING_MID.getTokenType(),
                TokenTypes.STRING_END.getTokenType(),
                TokenTypes.CHAR_LITERAL.getTokenType());
    }
}
