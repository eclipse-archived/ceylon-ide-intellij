import com.intellij.psi {
    PsiParameter
}
import com.redhat.ceylon.model.loader.mirror {
    VariableMirror
}

class PSIVariable(PsiParameter psi) 
        extends PSIAnnotatedMirror(psi)
        satisfies VariableMirror {
    
    type => PSIType(doWithLock(() => psi.type));
    
    string => "PSIVariable[``name``]";
}