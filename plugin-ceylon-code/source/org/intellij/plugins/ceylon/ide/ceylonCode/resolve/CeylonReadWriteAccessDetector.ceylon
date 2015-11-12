import com.intellij.codeInsight.highlighting {
    ReadWriteAccessDetector
}
import com.intellij.psi {
    PsiReference,
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonReadWriteAccessDetector() extends ReadWriteAccessDetector() {
    shared actual Access getExpressionAccess(PsiElement psiElement) {
        print(psiElement.parent.parent);
        
        return (psiElement.parent is CeylonPsi.AttributeDeclarationPsi
             || psiElement.parent.parent is CeylonPsi.SpecifierStatementPsi)
                then Access.\iWrite else Access.\iRead;
     }
    
    shared actual Access getReferenceAccess(PsiElement psiElement,
        PsiReference psiReference) {
        
        return getExpressionAccess(psiReference.element);
    }
    
    shared actual Boolean isDeclarationWriteAccess(PsiElement psiElement) {
        return if (is CeylonPsi.AttributeDeclarationPsi psiElement)
               then psiElement.ceylonNode.specifierOrInitializerExpression exists
               else false;
    }
    
    shared actual Boolean isReadWriteAccessible(PsiElement psiElement)
            => psiElement is CeylonPsi.AttributeDeclarationPsi;
}
