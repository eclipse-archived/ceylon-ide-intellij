import com.intellij.psi {
    PsiElement,
    PsiNamedElement,
    JavaPsiFacade {
        javaFacade=getInstance
    }
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

    function getSource(PsiElement clsClass)
            => ceylonSourceNavigator.getOriginalElements(clsClass)[0];

    shared actual PsiElement? getNavigationElement(ClsClassImpl clsClass) {
        if (exists source = getSource(clsClass)) {
            return source;
        }

        if (is ClsClassImpl parent = clsClass.parent,
            parent.name.endsWith("$impl")) {

            value parentNoImpl = parent.qualifiedName[0:parent.qualifiedName.size-5];
            value child = javaFacade(clsClass.project)
                .findClass(parentNoImpl, clsClass.resolveScope);

            if (is ClsClassImpl child,
                exists source = child.sourceMirrorClass) {

                for (cl in source.innerClasses) {
                    if (exists name = (cl of PsiNamedElement).name,
                        name == clsClass.name) {
                        return cl;
                    }
                }
            }
        }

        return null;
    }

    shared actual PsiElement? getNavigationElement(ClsMethodImpl clsMethod) {
        if (exists source = getSource(clsMethod)) {
            return source;
        }
        return null;
    }
}
