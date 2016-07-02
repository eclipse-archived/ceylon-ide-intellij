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
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeParameterMirror,
    TypeKind
}

import java.util {
    IdentityHashMap,
    Map,
    Arrays
}

shared class PSIType(psi,
    originatingTypes = IdentityHashMap<PsiType,PSIType?>(),
    enclosing = null)
        satisfies TypeMirror {

    shared PsiType psi;

    PSIType? enclosing;
    Map<PsiType,PSIType?> originatingTypes;

    variable String? cachedQualifiedName = null;
    
    originatingTypes.put(psi, null);
   
    componentType
            => if (is PsiArrayType psi)
               then PSIType(psi.componentType)
               else null;
    
    declaredClass
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
        else if (psi == PsiType.\ivoid) then TypeKind.\ivoid
        else TypeKind.null;
    }
    
    shared actual TypeKind kind {
        value kind
                = switch (psi)
                case (is PsiArrayType) TypeKind.array
                case (is PsiTypeVariable) TypeKind.typevar
                case (is PsiClassType)
                    if (is PsiClassReferenceType psi,
                        doWithLock(() => psi.resolve() is PsiTypeParameter))
                    then TypeKind.typevar
                    else TypeKind.declared
                case (is PsiWildcardType) TypeKind.wildcard
                case (is PsiMethodReferenceType) TypeKind.declared
                case (is PsiPrimitiveType) primitiveKind(psi)
                else null;
        
        if (exists kind) {
            return kind;
        }
        else {
            throw Exception("Unknown PsiType " +className(psi));
        }
    }

    lowerBound =
            if (is PsiWildcardType psi,
                psi.\isuper,
                exists lower = psi.bound)
            then PSIType(lower)
            else null;
    
    primitive => psi is PsiPrimitiveType;
    
    PsiClass? findClass(String name) {
        assert (is PsiClassReferenceType psi);
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
                    ".impl" in sb.string,
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

    qualifiedName => cachedQualifiedName else (cachedQualifiedName = doWithLock(() => computedQualifiedName));
    
    // TODO
    qualifyingType => null; //enclosing;
    
    raw = if (is PsiClassType psi) then doWithLock(() => psi.raw) else false;
    
    function getTypeArguments(PsiType type)
            => if (is PsiClassType type)
                then doWithLock(() => type.parameters)
                else null;

    shared actual TypeParameterMirror? typeParameter {
        if (is PsiTypeParameter|PsiClassReferenceType psi) {
            return PSITypeParameter(psi);
        }
        else {
            platformUtils.log(Status._ERROR, "Unsupported PSIType.typeParameter " + className(psi));
            return null;
        }
    }
    
    upperBound =
            if (is PsiWildcardType psi, psi.\iextends,
                exists upper = psi.bound)
            then PSIType(upper)
            else null;

    typeArguments
            => Arrays.asList<TypeMirror>(
                if (exists types = getTypeArguments(psi))
                for (t in types)
                    PSIType(t, originatingTypes, this));

    string => "PSIType[``qualifiedName``]";
    
}