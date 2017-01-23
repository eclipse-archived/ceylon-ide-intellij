import com.intellij.codeInsight {
    TargetElementEvaluatorEx2
}
import com.intellij.psi {
    PsiElement,
    PsiReference
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

shared class CeylonTargetElementEvaluator() extends TargetElementEvaluatorEx2() {

    getElementByReference(PsiReference ref, Integer flags) => null;

    getNamedElement(PsiElement element)
            => if (is CeylonPsi.IdentifierPsi id = element.parent,
                   is CeylonPsi.DeclarationPsi dec = id.parent)
                        then dec else null;
}
