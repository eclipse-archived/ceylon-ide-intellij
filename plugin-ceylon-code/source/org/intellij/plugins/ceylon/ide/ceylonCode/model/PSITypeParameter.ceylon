import com.redhat.ceylon.model.loader.mirror {
    TypeParameterMirror,
    TypeMirror
}
import java.util {
    List,
    ArrayList
}
import com.intellij.psi {
    PsiTypeParameter,
    PsiNamedElement
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}

class PSITypeParameter(PsiTypeParameter|PsiClassReferenceType psi) satisfies TypeParameterMirror {
    
    shared actual List<TypeMirror> bounds = ArrayList<TypeMirror>();

    if (is PsiTypeParameter psi) {
        doWithLock(() {
                psi.extendsList.referencedTypes.array.coalesced.each(
                    (bound) => bounds.add(PSIType(bound))
                );
        });
    }

    shared actual String name;
    if (is PsiNamedElement psi) {
        name = psi.name else "<unknown>";
    } else {
        name = psi.className;
    }
    
    string => "PSITypeParameter[``name``]";    
}