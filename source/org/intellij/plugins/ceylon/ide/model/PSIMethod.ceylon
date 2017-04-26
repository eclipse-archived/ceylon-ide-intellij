import com.intellij.psi {
    PsiMethod,
    PsiModifier,
    PsiType,
    PsiAnnotationMethod,
    PsiClass,
    SmartPsiElementPointer
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

shared class PSIMethod(SmartPsiElementPointer<out PsiMethod> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies MethodMirror {

    shared PsiMethod psi {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el;
    }

    Return doWithContainingClass<Return>(Return(PsiClass) func, Return default)
            => concurrencyManager.needReadAccess(() =>
                if (exists cc = psi.containingClass) then func(cc) else default);

    Boolean classIs(String cls)
            => doWithContainingClass((cc) => (cc.qualifiedName else "") == cls, false);

    Boolean computedIsOverriding
            =>  (classIs("ceylon.language.Identifiable") && psi.name in ["equals", "hashCode"])
            || !(classIs("ceylon.language.Object") && psi.name in ["equals", "hashCode", "toString"])
                && concurrencyManager.needIndexes(psi.project,
                    ()=> concurrencyManager.dontCancel(
                        () => SuperMethodsSearch.search(psi, null, true, false).findFirst())) exists;

    variable Boolean? lazyIsOverriding = null;
    shared Boolean isOverriding => lazyIsOverriding else (lazyIsOverriding = computedIsOverriding);

    Boolean computedIsOverloading
            => !classIs("ceylon.language.Exception")
            && doWithContainingClass((cc)
                => count { for (m in cc.findMethodsByName(psi.name, true))
                        m == psi ||
                           !m.modifierList.hasModifierProperty(PsiModifier.static)
                        && !m.modifierList.hasModifierProperty(PsiModifier.private)
                        && !m.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
                        && !MethodSignatureUtil.isSuperMethod(m, psi)
                     } > 1,
                false);
    
    variable Boolean? lazyIsOverloading = null;
    shared Boolean isOverloading => lazyIsOverloading else (lazyIsOverloading = computedIsOverloading);
    
    abstract =>
        psi.hasModifierProperty(PsiModifier.abstract)
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
    
    parameters
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<VariableMirror>(
                    for (p in psi.parameterList.parameters)
                        PSIVariable(pointer(p))));
    
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    returnType => concurrencyManager.needReadAccess(() => if (exists t = psi.returnType) then PSIType(t) else null);
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
    staticInit => false;
    
    typeParameters
            => Arrays.asList<TypeParameterMirror>(
                for (tp in psi.typeParameters)
                    PSITypeParameter(tp));
    
    variadic => psi.varArgs;

    string => "PSIMethod[``name``]";
    
    defaultMethod => psi.hasModifierProperty(PsiModifier.default);   
}