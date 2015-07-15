package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import org.intellij.plugins.ceylon.ide.psi.CeylonTokens;
import org.jetbrains.annotations.Nullable;

public class CeylonQuoteHandler extends SimpleTokenSetQuoteHandler implements MultiCharQuoteHandler {

    public CeylonQuoteHandler() {
        super(
                CeylonTokens.BACKTICK,
                CeylonTokens.STRING_LITERAL,
                CeylonTokens.STRING_INTERP,
                CeylonTokens.STRING_TEMPLATE,
                CeylonTokens.VERBATIM_STRING,
                CeylonTokens.CHAR_LITERAL
        );
    }

    @Override
    public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
        if (iterator.getTokenType() == CeylonTokens.STRING_TEMPLATE) {
            return false;
        }
        return super.isClosingQuote(iterator, offset);
    }

    @Override
    public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
        if (super.isOpeningQuote(iterator, offset)) {
            return true;
        }

        if (iterator.getTokenType() == CeylonTokens.STRING_TEMPLATE) {
            return iterator.getDocument().getText(TextRange.from(offset - 1, 2)).equals("``");
        }

        if (iterator.getTokenType() == CeylonTokens.VERBATIM_STRING) {
            return iterator.getDocument().getText(TextRange.from(offset - 2, 3)).equals("\"\"\"");
        }

        return false;
    }

    @Override
    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
        if (super.hasNonClosedLiteral(editor, iterator, offset)) {
            return true;
        }

        if (iterator.getTokenType() == CeylonTokens.VERBATIM_STRING) {
            if (iterator.getDocument().getText().substring(iterator.getStart(), offset + 1).equals("\"\"\"")) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public CharSequence getClosingQuote(HighlighterIterator iterator, int offset) {
        if (offset >= 3) {
            String possibleQuote = iterator.getDocument().getText(TextRange.from(offset - 3, 3));

            if (possibleQuote.equals("\"\"\"")) {
                return possibleQuote;
            }
        }
        return null;
    }
}
