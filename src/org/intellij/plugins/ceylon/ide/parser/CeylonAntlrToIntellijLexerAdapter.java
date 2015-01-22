package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognizerSharedState;
import org.intellij.plugins.ceylon.ide.psi.TokenTypes;
import org.jetbrains.annotations.NotNull;

/**
 * This is a wrapper for Ceylons's antlr lexer that conforms to the IntelliJ API.
 * <p/>
 * Note that this lexer is only capable of lexing the whole file and doesn't do incremental lexing. This is enough for parsing;
 * for syntax highlighting incremental lexing capabilities are required, and the JFlex-based {@link CeylonFlexLexerAdapter}
 * is used for this purpuse.
 */
public class CeylonAntlrToIntellijLexerAdapter extends LexerBase {
    private Lexer lexer;
    private RecognizerSharedState recognizerState = new RecognizerSharedState();
    private CharSequence buffer;
    private int endOffset;

    public CeylonAntlrToIntellijLexerAdapter() {
        this.lexer = new CeylonLexer(null, recognizerState);
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int state) {
        this.buffer = buffer;
        this.endOffset = endOffset;
        lexer.setCharStream(new ANTLRStringStream(buffer.toString()));
        lexer.nextToken();
    }

    @Override
    public int getState() {
        throw new UnsupportedOperationException("State not supported by this non-incremental lexer.");
    }

    @Override
    public IElementType getTokenType() {
        if (recognizerState.token == null) {
            return null;
        }
        IElementType eltType = TokenTypes.fromInt(recognizerState.token.getType());
        return eltType == null ? TokenTypes.EOF.getTokenType() : eltType;
    }

    @Override
    public int getTokenStart() {
        int index = recognizerState.tokenStartCharIndex;
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
    @NotNull
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}