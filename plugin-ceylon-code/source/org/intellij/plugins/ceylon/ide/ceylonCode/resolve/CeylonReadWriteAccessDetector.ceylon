import com.intellij.codeInsight.highlighting {
    ReadWriteAccessDetector
}
import com.intellij.psi {
    PsiReference,
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
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
            => if (is AttributeDeclarationPsi psiElement)
                then psiElement.ceylonNode.specifierOrInitializerExpression exists
            else if (is VariablePsi psiElement)
                then psiElement.ceylonNode.specifierExpression exists
            else false;
    
    isReadWriteAccessible(PsiElement psiElement)
            => psiElement is AttributeDeclarationPsi
                           | VariablePsi;

}
