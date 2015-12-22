import com.redhat.ceylon.model.loader.mirror {
    VariableMirror,
    TypeMirror,
    AnnotationMirror
}
import com.intellij.psi {
    PsiParameter,
    PsiNamedElement
}

class PSIVariable(PsiParameter psi) satisfies VariableMirror {
    
    // TODO duplicated from PSIMethod
    shared actual AnnotationMirror? getAnnotation(String name) {
        value ann = psi.modifierList.annotations.array.coalesced.find(
            (a) => a.qualifiedName == name
        );
        
        return if (exists ann) then PSIAnnotation(ann) else null;
    }
    
    shared actual String name = doWithLock(() => (psi of PsiNamedElement).name);
    
    shared actual TypeMirror type => PSIType(psi.type);
    
    string => "PSIVariable[``name``]";
}