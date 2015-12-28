import com.intellij.psi {
    PsiField,
    PsiModifier {
        ...
    }
}
import com.redhat.ceylon.model.loader.mirror {
    FieldMirror,
    TypeMirror
}

class PSIField(PsiField psi)
        extends PSIAnnotatedMirror(psi)
        satisfies FieldMirror {
    
    shared actual TypeMirror type = PSIType(psi.type);
    
    shared actual Boolean defaultAccess =>
            let (private = psi.hasModifierProperty(\iPRIVATE))
        !(private || protected || public);
    
    shared actual Boolean final => psi.hasModifierProperty(\iFINAL);
    
    shared actual Boolean protected => psi.hasModifierProperty(\iPROTECTED);
    
    shared actual Boolean public => psi.hasModifierProperty(\iPUBLIC);
    
    shared actual Boolean static => psi.hasModifierProperty(\iSTATIC);
    
}
