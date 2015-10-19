package org.intellij.plugins.ceylon.ide.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import gnu.trove.THashMap;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.ceylonHighlightingColors_;
import org.intellij.plugins.ceylon.ide.parser.CeylonFlexLexerAdapter;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CeylonHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys;

    private static ceylonHighlightingColors_ ceylonHighlightingColors = ceylonHighlightingColors_.get_();
    
    public static final TextAttributesKey KEYWORD_KEY = TextAttributesKey.createTextAttributesKey("CEYLON.KEYWORD", ceylonHighlightingColors.getKeyword());

    static {
        keys = new THashMap<>();

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
                TokenTypes.NONEMPTY.getTokenType(),
                TokenTypes.LET.getTokenType(),
                TokenTypes.NEW.getTokenType()
        );

        fillMap(keys, keywords, KEYWORD_KEY);

        keys.put(TokenTypes.MULTI_COMMENT.getTokenType(), ceylonHighlightingColors.getComment());
        keys.put(TokenTypes.LINE_COMMENT.getTokenType(), ceylonHighlightingColors.getComment());

        keys.put(TokenTypes.NATURAL_LITERAL.getTokenType(), ceylonHighlightingColors.getNumber());
        keys.put(TokenTypes.FLOAT_LITERAL.getTokenType(), ceylonHighlightingColors.getNumber());

        keys.put(TokenTypes.CHAR_LITERAL.getTokenType(), ceylonHighlightingColors.getChar());

        keys.put(CeylonTokens.STRING_LITERAL, ceylonHighlightingColors.getStrings());
        keys.put(CeylonTokens.STRING_INTERP, ceylonHighlightingColors.getStrings());
        keys.put(CeylonTokens.VERBATIM_STRING, ceylonHighlightingColors.getStrings());

        keys.put(TokenTypes.LPAREN.getTokenType(), ceylonHighlightingColors.getBrace());
        keys.put(TokenTypes.RPAREN.getTokenType(), ceylonHighlightingColors.getBrace());
        keys.put(TokenTypes.LBRACE.getTokenType(), ceylonHighlightingColors.getBrace());
        keys.put(TokenTypes.RBRACE.getTokenType(), ceylonHighlightingColors.getBrace());
        keys.put(TokenTypes.LBRACKET.getTokenType(), ceylonHighlightingColors.getBrace());
        keys.put(TokenTypes.RBRACKET.getTokenType(), ceylonHighlightingColors.getBrace());

        keys.put(TokenTypes.SEMICOLON.getTokenType(), ceylonHighlightingColors.getSemi());

        keys.put(TokenTypes.UIDENTIFIER.getTokenType(), ceylonHighlightingColors.getType());
        keys.put(TokenTypes.LIDENTIFIER.getTokenType(), ceylonHighlightingColors.getIdentifier());

        keys.put(TokenType.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CeylonFlexLexerAdapter();
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
