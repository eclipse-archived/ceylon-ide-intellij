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
    },
    SmartPsiElementPointer,
    SmartPointerManager,
    PsiElement
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    concurrencyManager {
        needReadAccess,
        needIndexes
    }
}

shared abstract class PSIAnnotatedMirror(psiPointer)
        satisfies AnnotatedMirror {

    SmartPsiElementPointer<out PsiModifierListOwner&PsiNamedElement> psiPointer;
    value psi {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el;
    }

    shared SmartPsiElementPointer<Psi> pointer<Psi>(Psi el) given Psi satisfies PsiElement
            => SmartPointerManager.getInstance(psiPointer.project).createSmartPsiElementPointer(el);

    variable Map<String,AnnotationMirror>? annotationMap = null;

    String? getAnnotationName(PsiAnnotation psi) {
        value qualifiedName = needReadAccess(() => psi.qualifiedName);
        if (!exists qualifiedName) {
            return null;
        }
        value resolveScope = psi.resolveScope;
        value facade = javaFacade(psi.project);

        if (exists cls = needIndexes(psi.project, () => facade.findClass(qualifiedName, resolveScope))) {
            assert(exists qName = cls.qualifiedName);
            return qName;
        }

        if ('.' in qualifiedName) {
            value parts = qualifiedName.split('.'.equals).sequence();
            assert(nonempty reversedParts = parts.reversed);

            if (exists clsName = needReadAccess(() {
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
            else (annotationMap = needReadAccess(()
                    => map {
                        if (exists ml = psi.modifierList)
                        for (a in ml.annotations)
                        if (exists name = getAnnotationName(a))
                            name -> PSIAnnotation(a)
                    }));

    getAnnotation(String name) => annotations[name];

    annotationNames => JavaMap(JavaStringMap(annotations)).keySet();

    name = needReadAccess(() => psi.name else "unknown");
}
