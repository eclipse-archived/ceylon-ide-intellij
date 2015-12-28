import com.intellij.psi {
    PsiParameter
}
import com.redhat.ceylon.model.loader.mirror {
    VariableMirror,
    TypeMirror
}

class PSIVariable(PsiParameter psi) 
        extends PSIAnnotatedMirror(psi)
        satisfies VariableMirror {
    
    shared actual TypeMirror type => PSIType(psi.type);
    
    string => "PSIVariable[``name``]";
}