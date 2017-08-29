import com.intellij.psi {
    PsiMethod,
    PsiModifier,
    PsiType,
    PsiAnnotationMethod,
    PsiClass,
    SmartPsiElementPointer,
    PsiParameter
}
import com.intellij.psi.search.searches {
    SuperMethodsSearch
}
import com.intellij.psi.util {
    MethodSignatureUtil
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader
}
import com.redhat.ceylon.model.loader.mirror {
    MethodMirror,
    TypeParameterMirror,
    VariableMirror
}

import java.util {
    Arrays
}

shared class PSIMethod
        extends PSIAnnotatedMirror
        satisfies MethodMirror {

    static value idMethods = ["equals", "hashCode"];
    static value objectMethods => ["equals", "hashCode", "toString"];

    SmartPsiElementPointer<out PsiMethod> psiPointer;

    shared new (SmartPsiElementPointer<out PsiMethod> psiPointer)
            extends PSIAnnotatedMirror(psiPointer) {
        this.psiPointer = psiPointer;
    }

    shared PsiMethod psi => get(psiPointer);

    Return doWithContainingClass<Return>(Return(PsiClass) func, Return default)
            => concurrencyManager.needReadAccess(() =>
                if (exists cc = psi.containingClass) then func(cc) else default);

    Boolean classIs(String className)
            => doWithContainingClass((cc) => (cc.qualifiedName else "") == className, false);

    value isIdentifiable => classIs("ceylon.language.Identifiable");
    value isObject = classIs("ceylon.language.Object");
    value isException = classIs("ceylon.language.Exception");

    shared late Boolean isOverriding
            = (isIdentifiable && psi.name in idMethods)
            || !(isObject && psi.name in objectMethods)
            && concurrencyManager.needIndexes(psi.project,
                    () => concurrencyManager.dontCancel(
                        () => SuperMethodsSearch.search(psi, null, true, false)
                                .findFirst())) exists;

    shared late Boolean isOverloading
            = !isException
            && doWithContainingClass((cc) {
                variable value count = 0;
                for (method in cc.findMethodsByName(psi.name, true)) {
                    if (method == psi ||
//                           !m.modifierList.hasModifierProperty(PsiModifier.static)
                           !method.modifierList.hasModifierProperty(PsiModifier.private)
                        && !method.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
                        && !MethodSignatureUtil.isSuperMethod(method, psi)) {
                        if (++count > 1) {
                            return true;
                        }
                    }
                }
                else {
                    return false;
                }
            }, false);
    
    abstract => psi.hasModifierProperty(PsiModifier.abstract)
            || doWithContainingClass(PsiClass.\iinterface, false);

    constructor => concurrencyManager.dontCancel(() => psi.constructor);
    
    declaredVoid => (psi.returnType else PsiType.null) == PsiType.\ivoid;
    
    default => if (is PsiAnnotationMethod meth = psi)
               then concurrencyManager.needReadAccess(() => meth.defaultValue exists)
               else false;

    value private => psi.hasModifierProperty(PsiModifier.private);

    defaultAccess => !(public || protected || private);
    
    enclosingClass => doWithContainingClass((cc) => PSIClass(pointer(cc)), null);
    
    final => psi.hasModifierProperty(PsiModifier.final);

    class PSIParameter(PsiParameter psiParameter)
            extends PSIAnnotatedMirror(outer.pointer(psiParameter))
            satisfies VariableMirror {

        type = PSIType(psiParameter.type);

        string => "PSIVariable[``name``]";
    }

    parameters
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<VariableMirror>(
                    for (parameter in psi.parameterList.parameters)
                        PSIParameter(parameter)));
    
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);

    static => psi.hasModifierProperty(PsiModifier.static);

    returnType => concurrencyManager.needReadAccess(()
            => if (exists type = psi.returnType) then PSIType(type) else null);
    
    staticInit => false;
    
    typeParameters
            => concurrencyManager.needReadAccess(() =>
                Arrays.asList<TypeParameterMirror>(
                    for (typeParam in psi.typeParameters)
                        PSITypeParameter(typeParam)));
    
    variadic => psi.varArgs;

    string => "PSIMethod[``name``]";
    
    defaultMethod => psi.hasModifierProperty(PsiModifier.default);
}