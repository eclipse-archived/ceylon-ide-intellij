import com.intellij.psi {
    PsiMethod,
    PsiModifier,
    PsiType,
    PsiAnnotationMethod,
    PsiClass
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

shared class PSIMethod(shared PsiMethod psi)
        extends PSIAnnotatedMirror(psi)
        satisfies MethodMirror {
    
    Return doWithContainingClass<Return>(Return(PsiClass) func, Return default)
            => concurrencyManager.needReadAccess(() => if (exists cc = psi.containingClass) then func(cc) else default);

    Boolean classIs(String cls)
            => doWithContainingClass((cc) => (cc.qualifiedName else "") == cls, false);

    Boolean computedIsOverriding
            =>  (classIs("ceylon.language.Identifiable") && psi.name in ["equals", "hashCode"])
            || !(classIs("ceylon.language.Object") && psi.name in ["equals", "hashCode", "toString"])
                && concurrencyManager.needIndexes(psi.project, ()=>SuperMethodsSearch.search(psi, null, true, false).findFirst()) exists;

    variable Boolean? lazyIsOverriding = null;
    shared Boolean isOverriding => lazyIsOverriding else (lazyIsOverriding = computedIsOverriding);

    Boolean computedIsOverloading
            => !classIs("ceylon.language.Exception")
            && doWithContainingClass((cc)
                => count { for (m in cc.findMethodsByName(psi.name, true))
                        m == psi ||
                           !m.modifierList.hasModifierProperty("static")
                        && !m.modifierList.hasModifierProperty("private")
                        && !m.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
                        && !MethodSignatureUtil.areOverrideEquivalent(psi, m)
                     } > 1,
                false);
    
    variable Boolean? lazyIsOverloading = null;
    shared Boolean isOverloading => lazyIsOverloading else (lazyIsOverloading = computedIsOverloading);
    
    abstract =>
        psi.hasModifierProperty(PsiModifier.abstract)
        || doWithContainingClass(PsiClass.\iinterface, false);
    
    constructor => psi.constructor;
    
    declaredVoid => (psi.returnType else PsiType.null) == PsiType.\ivoid;
    
    default => if (is PsiAnnotationMethod psi)
               then concurrencyManager.needReadAccess(() => psi.defaultValue exists)
               else false;
    
    defaultAccess => !(public || protected || psi.hasModifierProperty(PsiModifier.private));
    
    enclosingClass => doWithContainingClass(PSIClass, null);
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    parameters
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<VariableMirror>(
                    for (p in psi.parameterList.parameters)
                        PSIVariable(p)));
    
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