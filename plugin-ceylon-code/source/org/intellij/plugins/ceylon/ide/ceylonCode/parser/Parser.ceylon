import com.intellij.lang {
    ASTNode,
    ParserDefinition,
    PsiParser
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi.tree {
    TokenSet
}

import java.lang {
    UnsupportedOperationException
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

shared class CeylonParserDefinition() satisfies ParserDefinition {

    createLexer(Project project)
            => CeylonAntlrToIntellijLexerAdapter();

    fileNodeType => CeylonStubTypes.ceylonFile;

    createFile = CeylonFile;

    whitespaceTokens
            = TokenSet.create(TokenTypes.ws.tokenType);

    commentTokens
            = TokenSet.create(
                TokenTypes.lineComment.tokenType,
                TokenTypes.multiComment.tokenType);

    stringLiteralElements
            = TokenSet.create(
                TokenTypes.stringLiteral.tokenType,
                TokenTypes.stringStart.tokenType,
                TokenTypes.stringMid.tokenType,
                TokenTypes.stringEnd.tokenType,
                TokenTypes.charLiteral.tokenType);

    createElement(ASTNode node)
            => if (node.elementType == CeylonTypes.specifierStatement)
            then SpecifierStatementPsiIdOwner(node)
            else CeylonPsiFactory.createElement(node);

    spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
            => SpaceRequirements.may;

    shared actual PsiParser createParser(Project project) {
        throw UnsupportedOperationException("See IdeaCeylonParser");
    }

}
