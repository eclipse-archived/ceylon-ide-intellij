import ceylon.collection {
    HashMap,
    HashSet
}

import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}
import ceylon.interop.java {
    JavaSet,
    javaString
}
import java.lang {
    JString=String
}
import java.util {
    Set
}

shared class PSIAnnotatedMirror(PsiModifierListOwner&PsiNamedElement psi)
        satisfies AnnotatedMirror {
    
    value annotations = HashMap<String,AnnotationMirror>();

    doWithLock(() =>
        psi.modifierList?.annotations?.array?.coalesced?.each((a) {
            if (exists name = a.qualifiedName) {
                annotations.put(name, PSIAnnotation(a));
            }
        })
    );

    getAnnotation(String name)
            => annotations.get(name)
               else annotations.find((el) => el.key.replaceLast(".", "$") == name)?.item;

    shared actual Set<JString> annotationNames
            => JavaSet(HashSet {*annotations.keys.map((n) => javaString(n))});

    shared actual String name = doWithLock(() => psi.name else "unknown");
}