import com.intellij.psi {
    PsiMethod,
    PsiModifier,
    PsiType,
    PsiAnnotationMethod,
    PsiParameter,
    PsiClass
}
import com.intellij.psi.search.searches {
    SuperMethodsSearch
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader
}
import com.redhat.ceylon.model.loader.mirror {
    MethodMirror,
    TypeParameterMirror,
    VariableMirror,
    TypeMirror
}

import java.util {
    List,
    ArrayList
}
import com.intellij.psi.util {
    MethodSignatureUtil
}

shared class PSIMethod(shared PsiMethod psi)
        extends PSIAnnotatedMirror(psi)
        satisfies MethodMirror {
    
    Return doWithContainingClass<Return>(Return(PsiClass) func, Return default) {
        return doWithLock(() {
            if (exists cc = psi.containingClass) {
                return func(cc);
            } else {
                return default;
            }
        });
    }

    Boolean classIs(String cls) =>
            doWithContainingClass(
                (cc) => (cc.qualifiedName else "") == cls, false
            );
    
    variable Boolean? lazyIsOverriding = null;
    
    Boolean computedIsOverriding {
        if (classIs("ceylon.language.Identifiable"),
            psi.name in ["equals", "hashCode"]) {
            
            return true;
        } else if (classIs("ceylon.language.Object"),
            psi.name in ["equals", "hashCode", "toString"]) {
            
            return false;
        } else {
            return SuperMethodsSearch.search(psi, null, true, false)
                    .findFirst() exists;
        }
    }

    shared Boolean isOverriding => lazyIsOverriding else (lazyIsOverriding = computedIsOverriding);

    Boolean computedIsOverloading {
        if (classIs("ceylon.language.Exception")) {
            return false;
        } else {
            return doWithContainingClass((cc) {
                    return cc.findMethodsByName(psi.name, true).iterable.coalesced.filter(
                        (m) => (m == psi) || ( 
                               !m.modifierList.hasModifierProperty("static")
                            && !m.modifierList.hasModifierProperty("private")
                            && !m.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
                            && !MethodSignatureUtil.areOverrideEquivalent(psi, m)
                        )).size > 1;
            }, false);
        }
    }
    
    variable Boolean? lazyIsOverloading = null;
    
    shared Boolean isOverloading => lazyIsOverloading else (lazyIsOverloading = computedIsOverloading);
    
    abstract =>
        psi.hasModifierProperty(PsiModifier.abstract)
        || doWithContainingClass((cc) => cc.\iinterface, false);
    
    constructor => psi.constructor;
    
    declaredVoid => (psi.returnType else PsiType.null) == PsiType.\ivoid;
    
    default => if (is PsiAnnotationMethod psi)
               then doWithLock(() => psi.defaultValue exists)
               else false;
    
    defaultAccess => !(public || protected || psi.hasModifierProperty(PsiModifier.private));
    
    enclosingClass => doWithContainingClass((cc) => PSIClass(cc), null);
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    parameters => doWithLock(() =>
        mirror<VariableMirror,PsiParameter>(psi.parameterList.parameters, PSIVariable)
    );
    
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    shared actual TypeMirror? returnType
            => doWithLock(() => if (exists t = psi.returnType) then PSIType(t) else null);
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
    staticInit => false;
    
    shared actual List<TypeParameterMirror> typeParameters {
        value result = ArrayList<TypeParameterMirror>();
         
        psi.typeParameters.array.coalesced.each(
            (tp) => result.add(PSITypeParameter(tp))
        );
        
        return result;
    }
    
    variadic => psi.varArgs;

    string => "PSIMethod[``name``]";
    
    defaultMethod => psi.hasModifierProperty(PsiModifier.default);   
}