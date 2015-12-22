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

class PSITypeParameter(PsiTypeParameter psi) satisfies TypeParameterMirror {
    
    shared actual List<TypeMirror> bounds = ArrayList<TypeMirror>();
    
    psi.extendsList.referencedTypes.array.coalesced.each(
        (bound) => bounds.add(PSIType(bound))
    );
    
    shared actual String name = (psi of PsiNamedElement).name;
    
    string => "PSITypeParameter[``name``]";    
}