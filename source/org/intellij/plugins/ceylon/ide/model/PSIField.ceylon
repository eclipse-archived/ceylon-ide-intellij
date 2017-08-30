import com.intellij.psi {
    PsiField,
    PsiModifier,
    SmartPsiElementPointer
}
import com.redhat.ceylon.model.loader.mirror {
    FieldMirror
}
import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
}

class PSIField(SmartPsiElementPointer<PsiField> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies FieldMirror {

    shared PsiField psi => get(psiPointer);
    
    shared actual late PSIType type
            = needReadAccess(() => PSIType(psi.type));

    final => psi.hasModifierProperty(PsiModifier.final);

    static => psi.hasModifierProperty(PsiModifier.static);
    
}
