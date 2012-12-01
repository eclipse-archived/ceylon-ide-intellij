package org.intellij.plugins.ceylon.lang;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import org.intellij.plugins.ceylon.psi.CeylonTypes;

public class CeylonQuoteHandler extends SimpleTokenSetQuoteHandler {

    public CeylonQuoteHandler() {
        super(CeylonTypes.STRING_LITERAL, CeylonTypes.QUOTED_LITERAL);
    }
}
