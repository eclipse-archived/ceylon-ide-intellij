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
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.redhat.ceylon.ide.common.model {
    unknownClassMirror
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeKind
}

import java.util {
    Arrays
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
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


shared class PSIType(shared PsiType psi, PSIType? enclosing = null)
        satisfies TypeMirror {

    componentType
            => if (is PsiArrayType psi)
            then PSIType(psi.componentType)
            else null;
    
    declaredClass
            => if (is PsiClassType psi,
                   exists cls = concurrencyManager.needReadAccess(() => psi.resolve()))
            then PSIClass(pointer(cls))
            else unknownClassMirror;

    TypeKind primitiveKind(PsiPrimitiveType psi) {
        //can't use switch here because it refuses
        //to use the value equality of PsiType
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
                    = needReadAccess(()
                        => facade.findClass(canonicalText, resolveScope))) {
                assert (exists qualifiedName = cls.qualifiedName);
                return qualifiedName;
            }

            if ('.' in canonicalText) {
                value parts = canonicalText.split('.'.equals).sequence();
                value reversedParts = parts.reversed;

                if (exists clsName
                        = needReadAccess(()
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

    shared actual late String qualifiedName
            = needReadAccess(() => computedQualifiedName);
    
    // TODO
    qualifyingType => null; //enclosing;
    
    raw = if (is PsiClassType psi) then needReadAccess(() => psi.raw) else false;
    
    function getTypeArguments(PsiType type)
            => if (is PsiClassType type)
            then needReadAccess(() => type.parameters)
            else null;

    typeParameter
            => switch (psi)
            case (is PsiTypeParameter)
                needReadAccess(() => PSITypeParameter(psi))
            else case (is PsiClassReferenceType)
                needReadAccess(()
                    => if (is PsiTypeParameter param = psi.resolve())
                        then PSITypeParameter(param)
                        else null)
            else null;
    
    upperBound =
            if (is PsiWildcardType psi, psi.\iextends,
                exists upper = psi.bound)
            then PSIType(upper)
            else null;

    typeArguments
            => Arrays.asList<TypeMirror>(
                if (exists types = getTypeArguments(psi))
                for (psiType in types)
                PSIType(psiType, /*originatingTypes,*/ this));

    string => "PSIType[``qualifiedName``]";
    
}