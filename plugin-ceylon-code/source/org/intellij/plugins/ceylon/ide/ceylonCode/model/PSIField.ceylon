import com.intellij.psi {
    PsiField,
    PsiModifier
}
import com.redhat.ceylon.model.loader.mirror {
    FieldMirror
}

class PSIField(PsiField psi)
        extends PSIAnnotatedMirror(psi)
        satisfies FieldMirror {
    
    type = PSIType(doWithLock(() => psi.type));
    
    defaultAccess =>
            let (private = psi.hasModifierProperty(PsiModifier.private))
                !(private || protected || public);
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
}
