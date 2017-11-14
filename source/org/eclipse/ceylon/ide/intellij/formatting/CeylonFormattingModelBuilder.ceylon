/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.formatting {
    FormattingModel,
    FormattingModelBuilder,
    FormattingModelProvider,
    Indent
}
import com.intellij.lang {
    ASTNode
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
}

import org.eclipse.ceylon.ide.intellij.lang {
    CeylonLanguage
}

shared class CeylonFormattingModelBuilder() satisfies FormattingModelBuilder {

    shared actual FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        value containingFile = element.containingFile.viewProvider.getPsi(CeylonLanguage.instance);
        value fileNode = containingFile.node;
        value spacings = Spacings(settings);
        value block = CeylonBlock(fileNode, Indent.absoluteNoneIndent, spacings);
        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, block, settings);
    }

    getRangeAffectingIndent(PsiFile file, Integer offset, ASTNode elementAtOffset)
            => null;
}
