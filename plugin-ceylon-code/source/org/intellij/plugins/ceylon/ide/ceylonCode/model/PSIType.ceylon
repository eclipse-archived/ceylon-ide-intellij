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
    JavaPsiFacade {
        javaFacade=getInstance
    },
    PsiNameHelper
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}
import com.redhat.ceylon.ide.common.model {
    unknownClassMirror
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeParameterMirror,
    ClassMirror,
    TypeKind
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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}

shared class PSIType(shared PsiType psi, Map<PsiType,PSIType?> originatingTypes
        = IdentityHashMap<PsiType,PSIType?>(), PSIType? enclosing = null)
        satisfies TypeMirror {

    variable String? cachedQualifiedName = null;
    
   originatingTypes.put(psi, null);
   
   shared actual TypeMirror? componentType
            => if (is PsiArrayType psi)
               then PSIType(psi.componentType)
               else null;
    
    shared actual ClassMirror? declaredClass
            => if (is PsiClassType psi,
                   exists cls = doWithLock(() => psi.resolve() else null))
               then PSIClass(cls)
               else unknownClassMirror;

    TypeKind primitiveKind(PsiPrimitiveType psi) {
        return if (psi == PsiType.boolean) then TypeKind.boolean
        else if (psi == PsiType.byte) then TypeKind.byte
        else if (psi == PsiType.char) then TypeKind.char
        else if (psi == PsiType.short) then TypeKind.short
        else if (psi == PsiType.double) then TypeKind.double
        else if (psi == PsiType.int) then TypeKind.int
        else if (psi == PsiType.float) then TypeKind.float
        else if (psi == PsiType.long) then TypeKind.long
        else if (psi == PsiType.\iVOID) then TypeKind.\iVOID
        else TypeKind.null;
    }
    
    shared actual TypeKind kind {
        value kind = 
        if (is PsiArrayType psi) then TypeKind.array
        else if (is PsiTypeVariable psi) then TypeKind.typevar
        else if (is PsiClassReferenceType psi,
                 is PsiTypeParameter tp = doWithLock(() => psi.resolve() of PsiClass?))
            then TypeKind.typevar
        else if (is PsiWildcardType psi) then TypeKind.wildcard
        else if (is PsiClassReferenceType|PsiMethodReferenceType psi)
            then TypeKind.declared
        else if (is PsiClassType psi) then TypeKind.declared
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
        
        return doWithLock(() =>
            javaFacade(psi.reference.project).findClass(name, scope)
        );
    }
    
    String computedQualifiedName {
        value canonicalText = PsiNameHelper.getQualifiedClassName(psi.canonicalText, false);

        if (is PsiClassReferenceType psi) {
            if (exists cls = findClass(canonicalText)) {
                assert(exists qName = cls.qualifiedName);
                return qName; 
            }
            
            value parts = canonicalText.split('.'.equals);
            value facade = javaFacade(psi.reference.project);
            value clsName = doWithLock(() {
                variable value pkg = facade.findPackage("");
                value sb = StringBuilder();
                variable value lookingForPkg = true;
                
                for (part in parts) {
                    if (lookingForPkg,
                        exists _pkg = pkg,
                        exists subPkg = _pkg.subPackages.array.coalesced.find(
                        (el) => (el.name else "") == part)) {
                        pkg = subPkg;
                    } else {
                        lookingForPkg = false;
                        sb.append(part).append(".");
                    }
                }
                
                if (sb.endsWith(".")) {
                    sb.deleteTerminal(1);
                }
                if (exists _pkg = pkg,
                    exists cls = _pkg.findClassByShortName(sb.string, psi.resolveScope).array.first) {
                    return cls.qualifiedName;
                }
                // TODO not cool, we got an incorrect qualified name
                if (exists _pkg = pkg,
                    sb.string.contains(".impl"),
                    exists cls = _pkg.findClassByShortName(sb.string.replace(".impl", "$impl"), psi.resolveScope).array.first) {
                    return cls.qualifiedName;
                }
                if (exists _pkg = pkg,
                    exists cls = _pkg.findClassByShortName(sb.string.replaceLast(".", "$"), psi.resolveScope).array.first) {
                    return cls.qualifiedName;
                }
                return null;
            });
            
            if (exists clsName) {
                return clsName;
            }
        }
        
        return canonicalText;
    }

    shared actual String qualifiedName
            => cachedQualifiedName else (cachedQualifiedName = doWithLock(() => computedQualifiedName));
    
    // TODO
    shared actual TypeMirror? qualifyingType => null; //enclosing;
    
    shared actual Boolean raw =
            if (is PsiClassType psi)
            then doWithLock(() => psi.raw) else false;
    
    ObjectArray<PsiType>? getTypeArguments(PsiType type) {
        return switch(type)
        case (is PsiClassType) doWithLock(() => type.parameters)
        else null;
    }

    shared actual TypeParameterMirror? typeParameter {
        if (is PsiTypeParameter|PsiClassReferenceType psi) {
            return PSITypeParameter(psi);
        }
        else {
            platformUtils.log(Status._ERROR, "Unsupported PSIType.typeParameter " + className(psi));
            return null;
        }
    }
    
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