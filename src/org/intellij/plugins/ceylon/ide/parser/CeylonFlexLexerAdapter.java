package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

@Deprecated
public class CeylonFlexLexerAdapter extends FlexAdapter {
    public CeylonFlexLexerAdapter() {
        super(new CeylonFlexLexer((Reader) null));
    }
}
