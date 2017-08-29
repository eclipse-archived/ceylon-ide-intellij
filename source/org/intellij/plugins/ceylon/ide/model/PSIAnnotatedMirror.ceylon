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
    AnnotatedMirror
}

import java.lang {
    Str=String,
    Types {
        nativeString
    }
}
import java.util {
    HashMap
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
}

shared abstract class PSIAnnotatedMirror(psiPointer)
        satisfies AnnotatedMirror {

    SmartPsiElementPointer<out PsiModifierListOwner&PsiNamedElement> psiPointer;

    shared SmartPsiElementPointer<Psi> pointer<Psi>(Psi psiElement)
            given Psi satisfies PsiElement
            => SmartPointerManager.getInstance(psiPointer.project)
                .createSmartPsiElementPointer(psiElement);

    //eager:
    name = needReadAccess(() => get(psiPointer).name else "unknown");

    String? getAnnotationName(PsiAnnotation psi) {
        value qualifiedName = needReadAccess(() => psi.qualifiedName);
        if (!exists qualifiedName) {
            return null;
        }
        value resolveScope = psi.resolveScope;
        value facade = javaFacade(psi.project);

        if (!psi.project.isDisposed(),
            exists cls = facade.findClass(qualifiedName, resolveScope)) {
            assert(exists clsName = cls.qualifiedName);
            return clsName;
        }

        if ('.' in qualifiedName) {
            value parts = qualifiedName.split('.'.equals).sequence();
            value reversedParts = parts.reversed;

            if (exists foundClass
                    = searchForClass {
                        potentialClass = reversedParts.first;
                        potentialPackageParts = reversedParts.rest;
                        facade = facade;
                        resolveScope = resolveScope;
                    },
                exists foundName = foundClass.qualifiedName) {
                return foundName;
            }
        }
        return qualifiedName;
    }

    //lazy:
    late value annotations
            = needReadAccess(() {
                value map = HashMap<Str,PSIAnnotation>();
                if (exists ml = get(psiPointer).modifierList) {
                    for (ann in ml.annotations) {
                        if (exists name = getAnnotationName(ann)) {
                            map[nativeString(name)] = PSIAnnotation(ann);
                        }
                    }
                }
                return map;
            });

    getAnnotation(String name) => annotations[nativeString(name)];

    annotationNames => annotations.keySet();

}
