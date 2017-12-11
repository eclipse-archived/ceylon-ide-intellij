/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight.highlighting {
    ReadWriteAccessDetector
}
import com.intellij.psi {
    PsiReference,
    PsiElement
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonPsi {
        ...
    }
}

shared class CeylonReadWriteAccessDetector() extends ReadWriteAccessDetector() {

    getExpressionAccess(PsiElement psiElement)
            => (psiElement.parent
                    is AttributeDeclarationPsi
                     | VariablePsi
             || psiElement.parent.parent
                    is SpecifierStatementPsi
                     | AssignmentOpPsi
                     | PostfixOperatorExpressionPsi
                     | PrefixOperatorExpressionPsi)
            then Access.write
            else Access.read;
    
    getReferenceAccess(PsiElement psiElement, PsiReference psiReference)
            => getExpressionAccess(psiReference.element);
    
    isDeclarationWriteAccess(PsiElement psiElement)
            => switch (psiElement)
            case (is AttributeDeclarationPsi)
                psiElement.ceylonNode.specifierOrInitializerExpression exists
            else case (is VariablePsi)
                psiElement.ceylonNode.specifierExpression exists
            else false;
    
    isReadWriteAccessible(PsiElement psiElement)
            => psiElement is AttributeDeclarationPsi
                           | VariablePsi;

}
