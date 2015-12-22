import com.intellij.psi {
    PsiType,
    PsiArrayType,
    PsiTypeVariable,
    PsiWildcardType,
    PsiPrimitiveType,
    PsiMethodReferenceType,
    PsiClassType
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeParameterMirror,
    ClassMirror
}

import java.util {
    List,
    Collections
}

import javax.lang.model.type {
    TypeKind
}

class PSIType(PsiType psi)
        satisfies TypeMirror {
    
    shared actual TypeMirror? componentType
            => if (is PsiArrayType psi)
               then PSIType(psi.componentType)
               else null;
    
    shared actual ClassMirror? declaredClass
            => if (is PsiClassType psi,
                   exists cls = doWithLock(() => psi.resolve() else null))
               then PSIClass(cls)
               else null;

    TypeKind primitiveKind(PsiPrimitiveType psi) {
        return if (psi == PsiType.\iBOOLEAN) then TypeKind.\iBOOLEAN
        else if (psi == PsiType.\iBYTE) then TypeKind.\iBYTE
        else if (psi == PsiType.\iCHAR) then TypeKind.\iCHAR
        else if (psi == PsiType.\iSHORT) then TypeKind.\iSHORT
        else if (psi == PsiType.\iVOID) then TypeKind.\iVOID
        else if (psi == PsiType.\iDOUBLE) then TypeKind.\iDOUBLE
        else if (psi == PsiType.\iINT) then TypeKind.\iINT
        else if (psi == PsiType.\iFLOAT) then TypeKind.\iFLOAT
        else if (psi == PsiType.\iLONG) then TypeKind.\iLONG
        else TypeKind.\iNULL;
    }
    
    shared actual TypeKind kind {
        value kind = 
        if (is PsiArrayType psi) then TypeKind.\iARRAY
        else if (is PsiTypeVariable psi) then TypeKind.\iTYPEVAR
        else if (is PsiWildcardType psi) then TypeKind.\iWILDCARD
        else if (is PsiClassReferenceType|PsiMethodReferenceType psi)
            then TypeKind.\iDECLARED
        else if (is PsiPrimitiveType psi) then primitiveKind(psi)
        else null;
        
        if (exists kind) {
            return kind;
        }
        throw Exception("Unknown PsiType " + psi.string);
    }

    shared actual TypeMirror? lowerBound = 
            if (is PsiWildcardType psi,
                psi.\isuper,
                exists lower = psi.bound)
            then PSIType(lower)
            else null;
    
    shared actual Boolean primitive => psi is PsiPrimitiveType;
    
    shared actual String qualifiedName => psi.canonicalText;
    
    shared actual TypeMirror? qualifyingType {
        //if (is PsiClassType psi,
        //    exists cls = psi.resolve(),
        //    exists encl = getContextOfType(cls, javaClass<PsiClass>())) {
        //    
        //    //return PSIType(encl.);
        //}
        return null;
    }
    
    shared actual Boolean raw =
            if (is PsiClassType psi)
            then doWithLock(() => psi.raw) else false;
    
    shared actual List<TypeMirror> typeArguments
            => Collections.emptyList<TypeMirror>(); // TODO
    
    shared actual TypeParameterMirror? typeParameter
            => null; // TODO
    
    shared actual TypeMirror? upperBound =
            if (is PsiWildcardType psi,
                psi.\iextends,
                exists upper = psi.bound)
            then PSIType(upper)
            else null;

    string => "PSIType[``qualifiedName``]";
}