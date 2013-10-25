package org.intellij.plugins.ceylon.parser;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;
import org.intellij.plugins.ceylon.psi.TokenTypes;

public class CeylonLexerAdapter extends LexerBase {
    CeylonLexer lexer;
    RecognizerSharedState recognizerState = new RecognizerSharedState();
    private CharSequence buffer;
    private int endOffset;
    private int state;

    public CeylonLexerAdapter() {
        this.lexer = new CeylonLexer(null, recognizerState);
    }

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int state) {
        this.buffer = buffer;
        this.endOffset = endOffset;
        this.state = state;
        CharStream charStream = new MyCharStream(buffer);
        lexer.setCharStream(charStream);
        lexer.nextToken();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public IElementType getTokenType() {
        if (recognizerState.token == null) {
            return null;
        }

        IElementType eltType = TokenTypes.fromInt(recognizerState.token.getType());

        return eltType == null ? TokenTypes.EOF.getTokenType() : eltType;
//        return new IElementType("token-" + recognizerState.token.getType(), CeylonLanguage.INSTANCE);
    }

    @Override
    public int getTokenStart() {
        int index = recognizerState.tokenStartCharIndex ;
        return index == -1 ? 0 : index;
    }

    @Override
    public int getTokenEnd() {
        return getTokenStart() + recognizerState.token.getText().length();
    }

    @Override
    public void advance() {
        lexer.nextToken();
    }

    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}