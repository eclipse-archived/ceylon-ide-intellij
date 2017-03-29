import com.intellij.codeInsight.highlighting {
    ReadWriteAccessDetector
}
import com.intellij.psi {
    PsiReference,
    PsiElement
}

import org.intellij.plugins.ceylon.ide.psi {
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
