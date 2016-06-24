import com.intellij.codeInsight {
    TargetElementEvaluatorEx2
}
import com.intellij.psi {
    PsiElement,
    PsiReference
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonTargetElementEvaluator() extends TargetElementEvaluatorEx2() {

    shared actual Null getElementByReference(PsiReference ref, Integer flags) => null;

    shared actual PsiElement? getNamedElement(PsiElement element)
            => if (is CeylonPsi.IdentifierPsi id = element.parent,
                   is CeylonPsi.DeclarationPsi dec = id.parent)
                        then dec else null;
}
