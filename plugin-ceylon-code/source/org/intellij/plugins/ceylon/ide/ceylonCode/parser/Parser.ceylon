import com.intellij.lang {
    ASTNode,
    ParserDefinition,
    PsiParser
}
import com.intellij.lexer {
    Lexer
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    FileViewProvider,
    PsiElement,
    PsiFile
}
import com.intellij.psi.tree {
    IElementType,
    IFileElementType,
    TokenSet
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsiFactory,
    CeylonTypes,
    TokenTypes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    SpecifierStatementPsiIdOwner
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub {
    CeylonStubTypes
}
import org.jetbrains.annotations {
    NotNull
}
import java.lang {
    UnsupportedOperationException
}

shared class CeylonParserDefinition() satisfies ParserDefinition {

    TokenSet wsTokens = TokenSet.create(TokenTypes.ws.tokenType);

    createLexer(Project project) 
            => CeylonAntlrToIntellijLexerAdapter();

    fileNodeType => CeylonStubTypes.ceylonFile;

    whitespaceTokens => wsTokens;

    commentTokens
            => TokenSet.create(
                TokenTypes.lineComment.tokenType,
                TokenTypes.multiComment.tokenType);

    stringLiteralElements
            => TokenSet.create(
                TokenTypes.stringLiteral.tokenType,
                TokenTypes.stringStart.tokenType,
                TokenTypes.stringMid.tokenType,
                TokenTypes.stringEnd.tokenType,
                TokenTypes.charLiteral.tokenType);

    createElement(ASTNode node)
            => if (node.elementType == CeylonTypes.\iSPECIFIER_STATEMENT)
            then SpecifierStatementPsiIdOwner(node)
            else CeylonPsiFactory.createElement(node);

    createFile(FileViewProvider viewProvider)
            => CeylonFile(viewProvider);

    spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
            => SpaceRequirements.may;

    shared actual PsiParser createParser(Project project) {
        throw UnsupportedOperationException("See IdeaCeylonParser");
    }

}
