package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.TokenTypes;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NotNull;

public class CeylonParserDefinition implements ParserDefinition {

    public static final TokenSet WS_TOKENS = TokenSet.create(TokenTypes.WS.getTokenType());

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CeylonLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CeylonIdeaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return CeylonTypes.COMPILATION_UNIT;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WS_TOKENS;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.create(TokenTypes.LINE_COMMENT.getTokenType(), TokenTypes.MULTI_COMMENT.getTokenType());
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.create(TokenTypes.STRING_LITERAL.getTokenType(), TokenTypes.CHAR_LITERAL.getTokenType());
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new CeylonCompositeElementImpl(node, node.getUserData(CeylonIdeaParser.CEYLON_NODE_KEY));
//        return CeylonTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CeylonFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY; // TODO
    }
}
