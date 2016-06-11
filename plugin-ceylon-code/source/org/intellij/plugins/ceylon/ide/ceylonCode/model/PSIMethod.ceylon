import com.intellij.psi {
    PsiMethod,
    PsiModifier,
    PsiType,
    PsiAnnotationMethod,
    PsiParameter
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
    
    Boolean classIs(String cls) {
        return doWithLock(() => psi.containingClass.qualifiedName == cls);
    }
    
    variable Boolean? lazyIsOverriding = null;
    
    Boolean computedIsOverriding {
        if (classIs("ceylon.language.Identifiable"),
            ["equals", "hashCode"].contains(psi.name)) {
            
            return true;
        } else if (classIs("ceylon.language.Object"),
            ["equals", "hashCode", "toString"].contains(psi.name)) {
            
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
            return doWithLock(() {
                    return psi.containingClass.findMethodsByName(psi.name, true).iterable.coalesced.filter(
                        (m) => (m == psi) || ( 
                               !m.modifierList.hasModifierProperty("static")
                            && !m.modifierList.hasModifierProperty("private")
                            && !m.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
                            && !MethodSignatureUtil.areOverrideEquivalent(psi, m)
                        )).size > 1;
            });
        }
    }
    
    variable Boolean? lazyIsOverloading = null;
    
    shared Boolean isOverloading => lazyIsOverloading else (lazyIsOverloading = computedIsOverloading);
    
    abstract =>
        psi.hasModifierProperty(PsiModifier.abstract)
        || doWithLock(() => psi.containingClass.\iinterface);
    
    constructor => psi.constructor;
    
    declaredVoid => psi.returnType == PsiType.\ivoid;
    
    default => if (is PsiAnnotationMethod psi)
               then doWithLock(() => psi.defaultValue exists)
               else false;
    
    defaultAccess => !(public || protected || psi.hasModifierProperty(PsiModifier.private));
    
    enclosingClass => PSIClass(doWithLock(() => psi.containingClass));
    
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