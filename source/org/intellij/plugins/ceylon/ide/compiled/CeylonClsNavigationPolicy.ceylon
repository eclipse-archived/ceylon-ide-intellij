import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.impl.compiled {
    ClsCustomNavigationPolicyEx,
    ClsClassImpl,
    ClsMethodImpl,
    ClsFieldImpl
}

import org.intellij.plugins.ceylon.ide.resolve {
    ceylonSourceNavigator
}
import java.lang {
    overloaded
}

shared class CeylonClsNavigationPolicy() extends ClsCustomNavigationPolicyEx() {

    function getElement(PsiElement clsClass)
            => ceylonSourceNavigator.getOriginalElements(clsClass)[0];

    overloaded
    shared actual PsiElement? getNavigationElement(ClsMethodImpl clsMethod)
            => getElement(clsMethod);

    overloaded
    shared actual PsiElement? getNavigationElement(ClsFieldImpl clsField)
            => getElement(clsField);

    overloaded
    shared actual PsiElement? getNavigationElement(ClsClassImpl clsClass)
            => getElement(clsClass);

}
