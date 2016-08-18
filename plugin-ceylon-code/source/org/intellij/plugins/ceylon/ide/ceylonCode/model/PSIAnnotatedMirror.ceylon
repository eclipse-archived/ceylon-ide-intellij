import ceylon.interop.java {
    JavaMap,
    JavaStringMap
}

import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner,
    PsiAnnotation,
    JavaPsiFacade {
        javaFacade=getInstance
    }
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}

shared class PSIAnnotatedMirror(psi)
        satisfies AnnotatedMirror {

    PsiModifierListOwner&PsiNamedElement psi;

    variable Map<String,AnnotationMirror>? annotationMap = null;

    String? getAnnotationName(PsiAnnotation psi) {
        value qualifiedName = psi.qualifiedName;
        if (! exists qualifiedName) {
            return null;
        }
        value resolveScope = psi.resolveScope;
        value facade = javaFacade(psi.project);

        if (exists cls = concurrencyManager.needReadAccess(() =>
        facade.findClass(qualifiedName, resolveScope))) {
            assert(exists qName = cls.qualifiedName);
            return qName;
        }

        if ('.' in qualifiedName) {
            value parts = qualifiedName.split('.'.equals).sequence();
            assert(nonempty reversedParts = parts.reversed);

            if (exists clsName = concurrencyManager.needReadAccess(() {
                if (exists foundClass = searchForClass {
                    potentialClass = reversedParts.first;
                    potentialPackageParts = reversedParts.rest;
                    facade = facade;
                    resolveScope = resolveScope;
                }, exists qualifiedName = foundClass.qualifiedName) {
                    return qualifiedName;
                }

                return null;
            })) {
                return clsName;
            }
        }
        return qualifiedName;
    }

    value annotations
            => annotationMap
            else (annotationMap = concurrencyManager.needReadAccess(()
                    => map {
                        if (exists ml = psi.modifierList)
                        for (a in ml.annotations)
                        if (exists name = getAnnotationName(a))
                            name -> PSIAnnotation(a)
                    }));

    getAnnotation(String name) => annotations[name];

    annotationNames => JavaMap(JavaStringMap(annotations)).keySet();

    name = concurrencyManager.needReadAccess(() => psi.name else "unknown");
}
