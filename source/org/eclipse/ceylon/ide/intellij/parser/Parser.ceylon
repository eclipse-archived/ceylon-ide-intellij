/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.lang {
    ASTNode,
    ParserDefinition,
    PsiParser
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    FileViewProvider,
    PsiElement
}
import com.intellij.psi.tree {
    TokenSet
}

import java.lang {
    UnsupportedOperationException
}

import org.eclipse.ceylon.ide.intellij.lang {
    ceylonLanguage
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile,
    CeylonPsiFactory,
    CeylonTypes,
    TokenTypes,
    IdeaCeylonParser,
    CeylonPsi,
    parserConstants
}
import org.eclipse.ceylon.ide.intellij.psi.impl {
    SpecifierStatementPsiIdOwner
}

shared class CeylonParserDefinition() satisfies ParserDefinition {

    createLexer(Project project)
            => CeylonAntlrToIntellijLexerAdapter();

    fileNodeType = IdeaCeylonParser(ceylonLanguage);

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

    shared actual PsiElement createElement(ASTNode node) {
        if (node.elementType == CeylonTypes.specifierStatement) {
            return SpecifierStatementPsiIdOwner(node);
        } else {
            value elem = CeylonPsiFactory.createElement(node);
            if (is CeylonPsi.CompilationUnitPsi elem,
                exists postParseAction = node.getUserData(parserConstants.postParseAction)) {
                postParseAction();
                node.putUserData(parserConstants.postParseAction, null);
            }
            return elem;
        }
    }

    createFile(FileViewProvider viewProvider)
            => CeylonFile(viewProvider);

    spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
            => SpaceRequirements.may;

    shared actual PsiParser createParser(Project project) {
        throw UnsupportedOperationException("See IdeaCeylonParser");
    }

}
