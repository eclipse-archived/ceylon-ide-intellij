import com.intellij.psi {
    PsiParameter
}
import com.redhat.ceylon.model.loader.mirror {
    VariableMirror
}
import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
}

class PSIParameter(PsiParameter psiParameter)
        extends PSIAnnotatedMirror(pointer(psiParameter))
        satisfies VariableMirror {

    shared actual late PSIType type
            = needReadAccess(() => PSIType(psiParameter.type));

    string => "PSIVariable[``name``]";
}

