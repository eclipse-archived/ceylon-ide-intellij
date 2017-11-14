/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
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
import org.eclipse.ceylon.model.loader.mirror {
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

import org.eclipse.ceylon.ide.intellij.model {
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
