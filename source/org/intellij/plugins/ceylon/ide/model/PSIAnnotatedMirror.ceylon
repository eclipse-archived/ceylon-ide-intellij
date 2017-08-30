import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner,
    PsiAnnotation,
    JavaPsiFacade {
        javaFacade=getInstance
    },
    SmartPsiElementPointer,
    SmartPointerManager,
    PsiElement,
    PsiModifier
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AccessibleMirror
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

shared SmartPsiElementPointer<Psi> pointer<Psi>(Psi element)
        given Psi satisfies PsiElement
        => SmartPointerManager.getInstance(element.project)
            .createSmartPsiElementPointer(element);

shared abstract class PSIAnnotatedMirror(psiPointer)
        satisfies AnnotatedMirror & AccessibleMirror {

    SmartPsiElementPointer<out PsiModifierListOwner&PsiNamedElement> psiPointer;

    //eager:
    name = needReadAccess(() => get(psiPointer).name else "unknown");

    PsiModifierListOwner psi => get(psiPointer);

    public => psi.hasModifierProperty(PsiModifier.public);

    protected => psi.hasModifierProperty(PsiModifier.protected);

    value private => psi.hasModifierProperty(PsiModifier.private);

    defaultAccess => !(public || protected || private);

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
                if (exists modifiers = get(psiPointer).modifierList) {
                    for (ann in modifiers.annotations) {
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
