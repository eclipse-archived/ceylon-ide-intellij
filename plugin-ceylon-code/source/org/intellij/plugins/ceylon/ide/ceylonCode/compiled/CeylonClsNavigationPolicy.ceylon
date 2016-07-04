import com.intellij.psi {
    PsiElement,
    PsiNamedElement,
    JavaPsiFacade {
        javaFacade = getInstance
    }
}
import com.intellij.psi.impl.compiled {
    ClsCustomNavigationPolicyEx,
    ClsClassImpl
}

shared class CeylonClsNavigationPolicy() extends ClsCustomNavigationPolicyEx() {

    shared actual PsiElement? getNavigationElement(ClsClassImpl clsClass) {
        if (is ClsClassImpl parent = clsClass.parent,
            parent.name.endsWith("$impl")) {

            value parentNoImpl = parent.qualifiedName.measure(0, parent.qualifiedName.size - 5);
            value child = javaFacade(clsClass.project)
                .findClass(parentNoImpl, clsClass.resolveScope);

            if (is ClsClassImpl child,
                exists source = child.sourceMirrorClass) {

                value cand = source.innerClasses.iterable.find((cl) =>
                    if (exists name = (cl of PsiNamedElement).name, name == clsClass.name)
                    then true else false
                );
                return cand;
            }
        }

        return null;
    }
}
