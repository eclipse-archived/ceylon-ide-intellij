import com.intellij.psi {
    PsiType,
    PsiArrayType,
    PsiTypeVariable,
    PsiWildcardType,
    PsiPrimitiveType,
    PsiMethodReferenceType,
    PsiClassType,
    PsiTypeParameter,
    PsiClass,
    JavaPsiFacade
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeParameterMirror,
    ClassMirror
}

import java.lang {
    ObjectArray
}
import java.util {
    List,
    Collections,
    IdentityHashMap,
    Map
}

import javax.lang.model.type {
    TypeKind
}

class PSIType(PsiType psi, Map<PsiType,PSIType?> originatingTypes
        = IdentityHashMap<PsiType,PSIType?>(), PSIType? enclosing = null)
        satisfies TypeMirror {

   originatingTypes.put(psi, null);
   
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
        else if (is PsiClassReferenceType psi,
                 is PsiTypeParameter tp = doWithLock(() => psi.resolve() of PsiClass?))
            then TypeKind.\iTYPEVAR 
        else if (is PsiWildcardType psi) then TypeKind.\iWILDCARD
        else if (is PsiClassReferenceType|PsiMethodReferenceType psi)
            then TypeKind.\iDECLARED
        else if (is PsiClassType psi) then TypeKind.\iDECLARED
        else if (is PsiPrimitiveType psi) then primitiveKind(psi)
        else null;
        
        if (exists kind) {
            return kind;
        }
        throw Exception("Unknown PsiType " + className(psi));
    }

    shared actual TypeMirror? lowerBound = 
            if (is PsiWildcardType psi,
                psi.\isuper,
                exists lower = psi.bound)
            then PSIType(lower)
            else null;
    
    shared actual Boolean primitive => psi is PsiPrimitiveType;
    
    PsiClass? findClass(String name) {
        assert(is PsiClassReferenceType psi);
        value scope = psi.resolveScope;
        
        return  doWithLock(() {
            if (exists p = psi.reference?.project) {
                return JavaPsiFacade.getInstance(p)
                        .findClass(name, scope);            
            }
            return null;
        });
    }
    
    // TODO reeeeally ugly, but canonicalText is wrong (replaces $ with dots)
    shared actual String qualifiedName => psi.canonicalText;

    // TODO
    shared actual TypeMirror? qualifyingType => null; //enclosing;
    
    shared actual Boolean raw =
            if (is PsiClassType psi)
            then doWithLock(() => psi.raw) else false;
    
    ObjectArray<PsiType>? getTypeArguments(PsiType type) {
        return switch(type)
        case (is PsiClassType) type.parameters
        else null;
    }

    shared actual TypeParameterMirror? typeParameter =>
            if (is PsiTypeParameter psi)
            then PSITypeParameter(psi)
            else null;
    
    shared actual TypeMirror? upperBound =
            if (is PsiWildcardType psi,
                psi.\iextends,
                exists upper = psi.bound)
            then PSIType(upper)
            else null;

    shared actual List<TypeMirror> typeArguments
            => if (exists types = getTypeArguments(psi))
               then mirror<TypeMirror,PsiType>(types,
                    (t) => PSIType(t, originatingTypes, this)
               )
               else Collections.emptyList<TypeMirror>();

    string => "PSIType[``qualifiedName``]";
    
}