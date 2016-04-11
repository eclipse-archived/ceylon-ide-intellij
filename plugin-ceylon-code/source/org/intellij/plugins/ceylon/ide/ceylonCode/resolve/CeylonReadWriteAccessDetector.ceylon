import com.intellij.codeInsight.highlighting {
    ReadWriteAccessDetector
}
import com.intellij.psi {
    PsiReference,
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi {
        AttributeDeclarationPsi,
        SpecifierStatementPsi,
        VariablePsi
    }
}

shared class CeylonReadWriteAccessDetector() extends ReadWriteAccessDetector() {
    shared actual Access getExpressionAccess(PsiElement psiElement) {
        return (psiElement.parent is AttributeDeclarationPsi|VariablePsi
             || psiElement.parent.parent is CeylonPsi.SpecifierStatementPsi)
                then Access.\iWrite else Access.\iRead;
     }
    
    shared actual Access getReferenceAccess(PsiElement psiElement,
        PsiReference psiReference) {
        
        return getExpressionAccess(psiReference.element);
    }
    
    shared actual Boolean isDeclarationWriteAccess(PsiElement psiElement) {
        if (is AttributeDeclarationPsi psiElement) {
            return psiElement.ceylonNode.specifierOrInitializerExpression exists;
        }
        else if (is VariablePsi psiElement) {
            return psiElement.ceylonNode.specifierExpression exists;
        }
        else {
            return false;
        }
    }
    
    shared actual Boolean isReadWriteAccessible(PsiElement psiElement)
            => psiElement is AttributeDeclarationPsi|VariablePsi;
}
