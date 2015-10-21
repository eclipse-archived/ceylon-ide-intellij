package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
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
    private CharSequence buffer;
    private int endOffset;

    private CommonToken myToken;
    private int myStart;

    private boolean needsRecover = false;
    private int lastTokenEnd = -1;
    private CommonToken tokenAfterLastError = null;

    public CeylonAntlrToIntellijLexerAdapter() {
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int state) {
        this.buffer = buffer;
        this.myStart = startOffset;
        this.endOffset = endOffset;
        String text = buffer.subSequence(startOffset, endOffset).toString();
        lexer = new CeylonLexer(new ANTLRStringStream(text)) {
            @Override
            public void reportError(RecognitionException e) {
                super.reportError(e);
                needsRecover = true;
            }
        };
        myToken = null;
    }

    @Override
    public int getState() {
        return myToken != null ? myToken.getType() : 0;
    }

    @Override
    public IElementType getTokenType() {
        locateToken();
        if (myToken == null) {
            return null;
        }
        if (myToken.getType() == -2) {
            return TokenType.BAD_CHARACTER;
        }
        IElementType eltType = TokenTypes.fromInt(myToken.getType());
        return eltType == null ? TokenTypes.EOF.getTokenType() : eltType;
    }

    @Override
    public int getTokenStart() {
        locateToken();
        return myStart + myToken.getStartIndex();
    }

    @Override
    public int getTokenEnd() {
        locateToken();
        return myStart + myToken.getStopIndex() + 1;
    }

    @Override
    public void advance() {
        locateToken();
        myToken = null;
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

    private void locateToken() {
        if (myToken == null) {
            try {
                if (tokenAfterLastError != null) {
                    myToken = tokenAfterLastError;
                    tokenAfterLastError = null;
                } else {
                    myToken = (CommonToken) lexer.nextToken();
                }

                if (needsRecover) {
                    tokenAfterLastError = myToken;
                    myToken = buildErrorToken();
                    needsRecover = false;
                }
                lastTokenEnd = myToken.getStopIndex();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (myToken.getType() == CeylonLexer.EOF) {
                myToken = null;
            }
        }
    }

    private CommonToken buildErrorToken() {
        return new CommonToken(tokenAfterLastError.getInputStream(),
                -2,
                tokenAfterLastError.getChannel(),
                lastTokenEnd + 1,
                tokenAfterLastError.getStartIndex() - 1);
    }
}