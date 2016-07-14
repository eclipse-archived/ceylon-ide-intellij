import ceylon.interop.java {
    JavaMap,
    JavaStringMap
}

import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}

shared class PSIAnnotatedMirror(psi)
        satisfies AnnotatedMirror {

    PsiModifierListOwner&PsiNamedElement psi;

    variable Map<String,AnnotationMirror>? annotationMap = null;
    value annotations
            => annotationMap
            else (annotationMap = concurrencyManager.needReadAccess(()
                    => map {
                        if (exists ml = psi.modifierList)
                        for (a in ml.annotations)
                        if (exists name = a.qualifiedName)
                            name -> PSIAnnotation(a)
                    }));

    getAnnotation(String name)
            => annotations[name]
                //TODO make this nicer:
                else annotations.filterKeys((key) => key.replaceLast(".", "$") == name)
                                .first?.item;

    annotationNames => JavaMap(JavaStringMap(annotations)).keySet();

    name = concurrencyManager.needReadAccess(() => psi.name else "unknown");
}
