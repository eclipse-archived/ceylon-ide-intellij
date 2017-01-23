import com.intellij.codeInsight.editorActions {
    MultiCharQuoteHandler,
    SimpleTokenSetQuoteHandler
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.editor.highlighter {
    HighlighterIterator
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    TokenType
}

import java.lang {
    CharSequence,
    JString=String
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonTokens
}

shared class CeylonQuoteHandler()
        extends SimpleTokenSetQuoteHandler(
            CeylonTokens.backtick,
            CeylonTokens.stringLiteral,
            CeylonTokens.stringInterp,
            CeylonTokens.stringTemplate,
            CeylonTokens.verbatimString,
            CeylonTokens.charLiteral,
            TokenType.badCharacter)
        satisfies MultiCharQuoteHandler {

    function getText(HighlighterIterator iterator, Integer offset, Integer length)
            => iterator.document.getText(TextRange.from(offset, length));

    isClosingQuote(HighlighterIterator iterator, Integer offset)
            => iterator.tokenType != CeylonTokens.stringTemplate
            && super.isClosingQuote(iterator, offset);

    isOpeningQuote(HighlighterIterator iterator, Integer offset)
            => super.isOpeningQuote(iterator, offset)
            || iterator.tokenType == CeylonTokens.stringTemplate
            && getText(iterator, offset-1, 2) == "\`\`"
            || iterator.tokenType == CeylonTokens.verbatimString
            && getText(iterator, offset-2, 3) == "\"\"\"";

    hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, Integer offset)
            => super.hasNonClosedLiteral(editor, iterator, offset)
            || iterator.tokenType == CeylonTokens.verbatimString
            && getText(iterator, iterator.start, offset+1-iterator.start) == "\"\"\"";

    shared actual CharSequence? getClosingQuote(HighlighterIterator iterator, Integer offset) {
        if (offset>=3) {
            value possibleQuote = getText(iterator, offset-3, 3);
            if (possibleQuote == "\"\"\"") {
                return JString(possibleQuote);
            }
        }
        return null;
    }

    shared actual Boolean isNonClosedLiteral(HighlighterIterator iterator, CharSequence chars) {
        if (iterator.start+1 == chars.length()) {
            return true;
        }
        value nextChar = chars.charAt(iterator.start + 1);
        return nextChar != '\"' && nextChar != '\'';
    }
}
