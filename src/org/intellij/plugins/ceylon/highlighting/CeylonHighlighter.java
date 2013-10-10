package org.intellij.plugins.ceylon.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import gnu.trove.THashMap;
import org.intellij.plugins.ceylon.parser.CeylonLexer;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CeylonHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys;

    public static final TextAttributesKey KEYWORD_KEY = TextAttributesKey.createTextAttributesKey(
            "CEYLON.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
    );

    static {
        keys = new THashMap<IElementType, TextAttributesKey>();

        TokenSet keywords = TokenSet.create(
                CeylonTypes.KW_ABSTRACTS,
                CeylonTypes.KW_ADAPTS,
                CeylonTypes.KW_ASSIGN,
                CeylonTypes.KW_BREAK,
                CeylonTypes.KW_CASE,
                CeylonTypes.KW_CATCH,
                CeylonTypes.KW_CLASS,
                CeylonTypes.KW_CONTINUE,
                CeylonTypes.KW_ELSE,
                CeylonTypes.KW_EXISTS,
                CeylonTypes.KW_EXTENDS,
                CeylonTypes.KW_FINALLY,
                CeylonTypes.KW_FOR,
                CeylonTypes.KW_FUNCTION,
                CeylonTypes.KW_GIVEN,
                CeylonTypes.KW_IF,
                CeylonTypes.KW_IMPORT,
                CeylonTypes.KW_IN,
                CeylonTypes.KW_INTERFACE,
                CeylonTypes.KW_IS,
                CeylonTypes.KW_MODULE,
                CeylonTypes.KW_NONEMPTY,
                CeylonTypes.KW_OBJECT,
                CeylonTypes.KW_OF,
                CeylonTypes.KW_OUT,
                CeylonTypes.KW_OUTER,
                CeylonTypes.KW_PACKAGE,
                CeylonTypes.KW_RETURN,
                CeylonTypes.KW_SATISFIES,
                CeylonTypes.KW_SUPER,
                CeylonTypes.KW_SWITCH,
                CeylonTypes.KW_THEN,
                CeylonTypes.KW_THIS,
                CeylonTypes.KW_THROW,
                CeylonTypes.KW_TRY,
                CeylonTypes.KW_VALUE,
                CeylonTypes.KW_VOID,
                CeylonTypes.KW_WHILE
        );

        fillMap(keys, keywords, KEYWORD_KEY);

        keys.put(CeylonTypes.MULTI_LINE_COMMENT, DefaultLanguageHighlighterColors.DOC_COMMENT);
        keys.put(CeylonTypes.LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);

        keys.put(CeylonTypes.NATURAL_LITERAL, DefaultLanguageHighlighterColors.NUMBER);
        keys.put(CeylonTypes.FLOAT_LITERAL, DefaultLanguageHighlighterColors.NUMBER);
        keys.put(CeylonTypes.QUOTED_LITERAL, DefaultLanguageHighlighterColors.STRING);
        keys.put(CeylonTypes.CHAR_LITERAL, DefaultLanguageHighlighterColors.STRING);
        keys.put(CeylonTypes.STRING_LITERAL, DefaultLanguageHighlighterColors.STRING);

        keys.put(CeylonTypes.OP_LPAREN, DefaultLanguageHighlighterColors.PARENTHESES);
        keys.put(CeylonTypes.OP_RPAREN, DefaultLanguageHighlighterColors.PARENTHESES);
        keys.put(CeylonTypes.OP_LBRACE, DefaultLanguageHighlighterColors.BRACES);
        keys.put(CeylonTypes.OP_RBRACE, DefaultLanguageHighlighterColors.BRACES);
        keys.put(CeylonTypes.OP_LBRACKET, DefaultLanguageHighlighterColors.BRACKETS);
        keys.put(CeylonTypes.OP_RBRACKET, DefaultLanguageHighlighterColors.BRACKETS);
        keys.put(CeylonTypes.OP_BRACKETS, DefaultLanguageHighlighterColors.BRACES);

        keys.put(CeylonTypes.OP_DOT, DefaultLanguageHighlighterColors.DOT);
        keys.put(CeylonTypes.OP_SEMI_COLUMN, DefaultLanguageHighlighterColors.SEMICOLON);
        keys.put(CeylonTypes.OP_COMMA, DefaultLanguageHighlighterColors.COMMA);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CeylonLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (keys.containsKey(tokenType)) {
            return pack(keys.get(tokenType));
        }

        return EMPTY;
    }
}
