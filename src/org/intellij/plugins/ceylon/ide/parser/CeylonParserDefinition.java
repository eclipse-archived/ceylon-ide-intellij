package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiFactory;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.TokenTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.CeylonStubTypes;
import org.jetbrains.annotations.NotNull;

public class CeylonParserDefinition implements ParserDefinition {

    public static final TokenSet WS_TOKENS = TokenSet.create(TokenTypes.WS.getTokenType());

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CeylonAntlrToIntellijLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CeylonIdeaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return CeylonStubTypes.CEYLON_FILE;
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
        return TokenSet.create(
                TokenTypes.STRING_LITERAL.getTokenType(),
                TokenTypes.STRING_START.getTokenType(),
                TokenTypes.STRING_MID.getTokenType(),
                TokenTypes.STRING_END.getTokenType(),
                TokenTypes.CHAR_LITERAL.getTokenType());
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        final IElementType type = node.getElementType();
        return /*type == CeylonTypes.CLASS_DEFINITION || type == CeylonTypes.INTERFACE_DEFINITION
                ? new StubbedClassDefinitionPsiImpl(node)
                :*/ CeylonPsiFactory.createElement(node);
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
