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
    
    shared actual TypeMirror type = PSIType(doWithLock(() => psi.type));
    
    shared actual Boolean defaultAccess =>
            let (private = psi.hasModifierProperty(private))
        !(private || protected || public);
    
    shared actual Boolean final => psi.hasModifierProperty(final);
    
    shared actual Boolean protected => psi.hasModifierProperty(protected);
    
    shared actual Boolean public => psi.hasModifierProperty(public);
    
    shared actual Boolean static => psi.hasModifierProperty(static);
    
}
