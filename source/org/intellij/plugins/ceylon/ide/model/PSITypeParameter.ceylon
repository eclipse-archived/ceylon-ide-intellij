import com.intellij.psi {
    PsiTypeParameter,
    PsiNamedElement
}
import com.redhat.ceylon.model.loader.mirror {
    TypeParameterMirror,
    TypeMirror
}

import java.util {
    ArrayList
}

// We don't use `SmartPsiElementPointer`s because `psi` is accessed eagerly
class PSITypeParameter(PsiTypeParameter psi)
        satisfies TypeParameterMirror {
    
    bounds = ArrayList<TypeMirror>();

    for (bound in psi.extendsList.referencedTypes) {
        bounds.add(PSIType(bound));
    }

    name = (psi of PsiNamedElement).name else "<unknown>";
    
    string => "PSITypeParameter[``name``]";    
}