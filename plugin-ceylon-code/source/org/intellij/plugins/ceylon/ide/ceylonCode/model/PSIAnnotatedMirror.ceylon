import ceylon.collection {
    HashMap
}

import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}

class PSIAnnotatedMirror(PsiModifierListOwner&PsiNamedElement psi)
        satisfies AnnotatedMirror {
    
    value annotations = HashMap<String,AnnotationMirror>();
    
    psi.modifierList?.annotations?.array?.coalesced?.each(
        (a) => annotations.put(a.qualifiedName, PSIAnnotation(a))
    );
    
    shared actual AnnotationMirror? getAnnotation(String name) 
            => annotations.get(name)
               else annotations.find((el) => el.key.replaceLast(".", "$") == name)?.item;
    
    shared actual String name = doWithLock(() => psi.name else "unknown");
}