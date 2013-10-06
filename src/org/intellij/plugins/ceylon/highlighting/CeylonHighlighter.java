package org.intellij.plugins.ceylon.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import gnu.trove.THashMap;
import org.intellij.plugins.ceylon.parser.CeylonLexer;
import org.intellij.plugins.ceylon.parser.CeylonLexerAdapter;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.TokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CeylonHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys;

    public static final TextAttributesKey KEYWORD_KEY = TextAttributesKey.createTextAttributesKey(
            "CEYLON.KEYWORD", SyntaxHighlighterColors.KEYWORD.getDefaultAttributes()
    );

    static {
        keys = new THashMap<IElementType, TextAttributesKey>();

        TokenSet keywords = TokenSet.create(
            TokenTypes.ASSEMBLY.getTokenType(),
            TokenTypes.MODULE.getTokenType(),
            TokenTypes.PACKAGE.getTokenType(),
            TokenTypes.IMPORT.getTokenType(),
            TokenTypes.ALIAS.getTokenType(),
            TokenTypes.CLASS_DEFINITION.getTokenType(),
            TokenTypes.INTERFACE_DEFINITION.getTokenType(),
            TokenTypes.OBJECT_DEFINITION.getTokenType(),
            TokenTypes.TYPE_CONSTRAINT.getTokenType(), // given
            TokenTypes.VALUE_MODIFIER.getTokenType(),
            TokenTypes.ASSIGN.getTokenType(),
            TokenTypes.VOID_MODIFIER.getTokenType(),
            TokenTypes.FUNCTION_MODIFIER.getTokenType(),
            TokenTypes.CASE_TYPES.getTokenType(), // of
            TokenTypes.EXTENDS.getTokenType(),
            TokenTypes.SATISFIES.getTokenType(),
            TokenTypes.ABSTRACTED_TYPE.getTokenType(),
            TokenTypes.IN_OP.getTokenType(),
            TokenTypes.OUT.getTokenType(),
            TokenTypes.RETURN.getTokenType(),
            TokenTypes.BREAK.getTokenType(),
            TokenTypes.CONTINUE.getTokenType(),
            TokenTypes.THROW.getTokenType(),
            TokenTypes.ASSERT.getTokenType(),
            TokenTypes.DYNAMIC.getTokenType(),
            TokenTypes.IF_CLAUSE.getTokenType(),
            TokenTypes.ELSE_CLAUSE.getTokenType(),
            TokenTypes.SWITCH_CLAUSE.getTokenType(),
            TokenTypes.CASE_CLAUSE.getTokenType(),
            TokenTypes.FOR_CLAUSE.getTokenType(),
            TokenTypes.WHILE_CLAUSE.getTokenType(),
            TokenTypes.TRY_CLAUSE.getTokenType(),
            TokenTypes.CATCH_CLAUSE.getTokenType(),
            TokenTypes.FINALLY_CLAUSE.getTokenType(),
            TokenTypes.THEN_CLAUSE.getTokenType(),
            TokenTypes.THIS.getTokenType(),
            TokenTypes.OUTER.getTokenType(),
            TokenTypes.SUPER.getTokenType(),
            TokenTypes.IS_OP.getTokenType(),
            TokenTypes.EXISTS.getTokenType(),
            TokenTypes.NONEMPTY.getTokenType()
        );

        fillMap(keys, keywords, KEYWORD_KEY);

        keys.put(TokenTypes.MULTI_COMMENT.getTokenType(), SyntaxHighlighterColors.DOC_COMMENT);
        keys.put(TokenTypes.LINE_COMMENT.getTokenType(), SyntaxHighlighterColors.LINE_COMMENT);

        keys.put(TokenTypes.NATURAL_LITERAL.getTokenType(), SyntaxHighlighterColors.NUMBER);
        keys.put(TokenTypes.FLOAT_LITERAL.getTokenType(), SyntaxHighlighterColors.NUMBER);
        keys.put(TokenTypes.CHAR_LITERAL.getTokenType(), SyntaxHighlighterColors.STRING);
        keys.put(TokenTypes.STRING_LITERAL.getTokenType(), SyntaxHighlighterColors.STRING);
        keys.put(TokenTypes.VERBATIM_STRING.getTokenType(), SyntaxHighlighterColors.STRING);

        keys.put(TokenTypes.LPAREN.getTokenType(), SyntaxHighlighterColors.PARENTHS);
        keys.put(TokenTypes.RPAREN.getTokenType(), SyntaxHighlighterColors.PARENTHS);
        keys.put(TokenTypes.LBRACE.getTokenType(), SyntaxHighlighterColors.BRACES);
        keys.put(TokenTypes.RBRACE.getTokenType(), SyntaxHighlighterColors.BRACES);
        keys.put(TokenTypes.LBRACKET.getTokenType(), SyntaxHighlighterColors.BRACKETS);
        keys.put(TokenTypes.RBRACKET.getTokenType(), SyntaxHighlighterColors.BRACKETS);

        keys.put(TokenTypes.MEMBER_OP.getTokenType(), SyntaxHighlighterColors.DOT);
        keys.put(TokenTypes.SEMICOLON.getTokenType(), SyntaxHighlighterColors.JAVA_SEMICOLON);
        keys.put(TokenTypes.COMMA.getTokenType(), SyntaxHighlighterColors.COMMA);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CeylonLexerAdapter();
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
