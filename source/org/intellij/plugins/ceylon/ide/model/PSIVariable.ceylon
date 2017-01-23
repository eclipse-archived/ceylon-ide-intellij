import com.intellij.psi {
    PsiParameter,
    SmartPsiElementPointer,
    PsiType
}
import com.redhat.ceylon.model.loader.mirror {
    VariableMirror
}

class PSIVariable(SmartPsiElementPointer<PsiParameter> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies VariableMirror {

    PsiType psiType {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el.type;
    }
    type => PSIType(concurrencyManager.needReadAccess(() => psiType));
    
    string => "PSIVariable[``name``]";
}