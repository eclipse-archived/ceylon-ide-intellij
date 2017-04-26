import com.intellij.psi {
    PsiField,
    PsiModifier,
    SmartPsiElementPointer
}
import com.redhat.ceylon.model.loader.mirror {
    FieldMirror
}

class PSIField(SmartPsiElementPointer<PsiField> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies FieldMirror {

    PsiField psi {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el;
    }
    
    type = PSIType(concurrencyManager.needReadAccess(() => psi.type));

    value private => psi.hasModifierProperty(PsiModifier.private)

    defaultAccess => !(private || protected || public);
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
}
