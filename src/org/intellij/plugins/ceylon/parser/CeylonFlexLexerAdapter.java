package org.intellij.plugins.ceylon.parser;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CeylonFlexLexerAdapter extends FlexAdapter {
    public CeylonFlexLexerAdapter() {
        super(new CeylonFlexLexer((Reader) null));
    }
}
