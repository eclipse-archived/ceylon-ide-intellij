import com.intellij.psi {
    PsiField,
    PsiModifier {
        ...
    },
    PsiNamedElement
}
import com.redhat.ceylon.model.loader.mirror {
    FieldMirror,
    TypeMirror,
    AnnotationMirror
}

class PSIField(PsiField psi)
        satisfies FieldMirror {
    
    value annotations = psi.modifierList.annotations.array.coalesced.map(
        (a) => PSIAnnotation(a)
    );

    shared actual TypeMirror type = PSIType(psi.type);
    
    shared actual Boolean defaultAccess =>
            let (private = psi.hasModifierProperty(\iPRIVATE))
        !(private || protected || public);
    
    shared actual Boolean final => psi.hasModifierProperty(\iFINAL);
    
    shared actual AnnotationMirror? getAnnotation(String name)
            => annotations.find((_) => _.psi.qualifiedName == name)
          else annotations.find((_) => _.psi.qualifiedName.replaceLast(".", "$") == name);
    
    shared actual String name => (psi of PsiNamedElement).name;
    
    shared actual Boolean protected => psi.hasModifierProperty(\iPROTECTED);
    
    shared actual Boolean public => psi.hasModifierProperty(\iPUBLIC);
    
    shared actual Boolean static => psi.hasModifierProperty(\iSTATIC);
    
}
