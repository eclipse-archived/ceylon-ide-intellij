import com.intellij.lexer {
    LexerBase
}
import com.intellij.psi {
    TokenType
}
import com.intellij.psi.tree {
    IElementType
}
import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonLexer,
    CeylonInterpolatingLexer
}

import java.lang {
    CharSequence
}

import org.antlr.runtime {
    ANTLRStringStream,
    CommonToken,
    RecognitionException,
    TokenSource
}
import org.intellij.plugins.ceylon.ide.psi {
    TokenTypes
}

shared class CeylonAntlrToIntellijLexerAdapter() extends LexerBase() {

    variable late CharSequence buffer;
    variable late TokenSource lexer;
    
    variable Integer endOffset = -1;
    variable CommonToken? myToken = null;
    variable Integer myStart = -1;
    variable Boolean needsRecover = false;
    variable Integer lastTokenEnd = -1;
    variable CommonToken? tokenAfterLastError = null;


    shared actual void start(CharSequence buffer, Integer startOffset, Integer endOffset, Integer state) {
        this.buffer = buffer;
        this.myStart = startOffset;
        this.endOffset = endOffset;
        needsRecover = false;
        lastTokenEnd = - 1;
        tokenAfterLastError = null;
        String textFragment = buffer.subSequence(startOffset, endOffset).string;
        lexer = CeylonInterpolatingLexer(object extends CeylonLexer(ANTLRStringStream(textFragment)) {
            shared actual void reportError(RecognitionException e) {
                super.reportError(e);
                needsRecover = true;
            }
        });
        myToken = null;
    }

    state => if (exists token = myToken) then token.type else 0;

    bufferSequence => buffer;

    bufferEnd => endOffset;

    shared actual IElementType? tokenType {
        locateToken();
        if (exists token = myToken) {
            if (token.type == - 2) {
                return TokenType.badCharacter;
            }
            return if (exists eltType = TokenTypes.fromInt(token.type)) 
                then eltType else TokenTypes.eof.tokenType;
        }
        else {
            return null;
        }
    }

    shared actual Integer tokenStart {
        locateToken();
        assert (exists token = myToken);
        return myStart + token.startIndex;
    }

    shared actual Integer tokenEnd {
        locateToken();
        assert (exists token = myToken);
        return myStart + token.stopIndex + 1;
    }

    shared actual void advance() {
        locateToken();
        myToken = null;
    }

    function buildErrorToken() {
        assert (exists token = tokenAfterLastError);
        return CommonToken(token.inputStream, 
            -2, 
            token.channel, 
            lastTokenEnd + 1, 
            token.startIndex - 1);
    }
    
    void locateToken() {
        if (!myToken exists) {
            try {
                if (tokenAfterLastError exists) {
                    myToken = tokenAfterLastError;
                    tokenAfterLastError = null;
                } else {
                    assert (is CommonToken token = lexer.nextToken());
                    myToken = token;
                }
                if (needsRecover) {
                    tokenAfterLastError = myToken;
                    myToken = buildErrorToken();
                    needsRecover = false;
                }
                assert (exists token = myToken);
                lastTokenEnd = token.stopIndex;
                if (token.type == CeylonLexer.eof) {
                    myToken = null;
                }
            }
            catch (e) {
                e.printStackTrace();
            }
        }
    }

}
