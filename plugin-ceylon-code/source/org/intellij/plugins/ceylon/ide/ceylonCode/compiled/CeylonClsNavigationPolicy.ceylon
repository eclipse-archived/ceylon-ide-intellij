import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.impl.compiled {
    ClsCustomNavigationPolicyEx,
    ClsClassImpl,
    ClsMethodImpl
}

import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    ceylonSourceNavigator
}

shared class CeylonClsNavigationPolicy() extends ClsCustomNavigationPolicyEx() {

    function getElement(PsiElement clsClass)
            => ceylonSourceNavigator.getOriginalElements(clsClass)[0];

    shared actual PsiElement? getNavigationElement(ClsMethodImpl clsMethod)
            => getElement(clsMethod);

    shared actual PsiElement? getNavigationElement(ClsClassImpl clsClass) {
        if (exists source = getElement(clsClass)) {
            return source;
        }

        return null;
    }

}
