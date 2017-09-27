import com.intellij.psi {
    PsiField,
    PsiModifier,
    SmartPsiElementPointer
}
import org.eclipse.ceylon.model.loader.mirror {
    FieldMirror
}
import org.eclipse.ceylon.ide.intellij.model {
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
