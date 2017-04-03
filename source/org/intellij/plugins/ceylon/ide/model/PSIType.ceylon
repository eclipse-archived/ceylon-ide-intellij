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
    PsiNameHelper,
    SmartPointerManager
}
import com.intellij.psi.impl.source {
    PsiClassReferenceType
}
import com.intellij.psi.search {
    GlobalSearchScope
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

shared PsiClass? searchForClass(String potentialClass, [String*] potentialPackageParts,
        JavaPsiFacade facade, GlobalSearchScope resolveScope) {

    String prefixedPotentialClass
            = StringBuilder()
            .appendCharacter('$')
            .append(potentialClass).string;

    if (exists pkg = facade.findPackage(".".join(potentialPackageParts.reversed))) {
        return pkg.findClassByShortName(potentialClass, resolveScope).array.first
          else pkg.findClassByShortName(prefixedPotentialClass, resolveScope).array.first;
    }
    else if (nonempty potentialPackageParts) {

        if (exists first = potentialClass.first,
            first == 'i' && potentialClass == "impl" ||
            first == 'a' && (potentialClass == "annotation$" ||
                             potentialClass == "annotations$"),
            exists found
                    = searchForClass {
                        potentialClass
                                = StringBuilder()
                                .append(potentialPackageParts.first)
                                .appendCharacter('$')
                                .append(potentialClass).string;
                        potentialPackageParts = potentialPackageParts.rest;
                        facade = facade;
                        resolveScope = resolveScope;
                    }) {
            return found;
        }

        if (exists parentClass
                = searchForClass {
                    potentialClass = potentialPackageParts.first;
                    potentialPackageParts = potentialPackageParts.rest;
                    facade = facade;
                    resolveScope = resolveScope;
                },
            exists found
                    = parentClass.findInnerClassByName(potentialClass, false)
                    else parentClass.findInnerClassByName(prefixedPotentialClass, false)) {
            return found;
        }

        return null;

    }
    else {
        return null;
    }
}


shared class PSIType(psi,
    originatingTypes = IdentityHashMap<PsiType,PSIType?>(),
    enclosing = null)
        satisfies TypeMirror {

    shared PsiType psi;

    PSIType? enclosing;
    Map<PsiType,PSIType?> originatingTypes;

    variable String? cachedQualifiedName = null;
    
    originatingTypes[psi] = null;
   
    componentType
            => if (is PsiArrayType psi)
               then PSIType(psi.componentType)
               else null;
    
    declaredClass
            => if (is PsiClassType psi,
                   exists cls = concurrencyManager.needReadAccess(() => psi.resolve()))
               then PSIClass(SmartPointerManager.getInstance(cls.project).createSmartPsiElementPointer(cls))
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
                        concurrencyManager.needReadAccess(() => psi.resolve() is PsiTypeParameter))
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

    String computedQualifiedName {
        value canonicalText
                = PsiNameHelper.getQualifiedClassName(psi.canonicalText, false);

        if (is PsiClassReferenceType psi) {
            value resolveScope = psi.resolveScope;
            value facade = javaFacade(psi.reference.project);

            if (!psi.reference.project.isDisposed(),
                exists cls
                    = concurrencyManager.needReadAccess(()
                        => facade.findClass(canonicalText, resolveScope))) {
                assert (exists qualifiedName = cls.qualifiedName);
                return qualifiedName;
            }

            if ('.' in canonicalText) {
                value parts = canonicalText.split('.'.equals).sequence();
                value reversedParts = parts.reversed;

                if (exists clsName
                        = concurrencyManager.needReadAccess(()
                            => searchForClass {
                                potentialClass = reversedParts.first;
                                potentialPackageParts = reversedParts.rest;
                                facade = facade;
                                resolveScope = resolveScope;
                            }?.qualifiedName)) {
                    return clsName;
                }
            }
        }

        return canonicalText;
    }

    qualifiedName
            => cachedQualifiedName
            else (cachedQualifiedName = concurrencyManager.needReadAccess(() => computedQualifiedName));
    
    // TODO
    qualifyingType => null; //enclosing;
    
    raw = if (is PsiClassType psi) then concurrencyManager.needReadAccess(() => psi.raw) else false;
    
    function getTypeArguments(PsiType type)
            => if (is PsiClassType type)
                then concurrencyManager.needReadAccess(() => type.parameters)
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