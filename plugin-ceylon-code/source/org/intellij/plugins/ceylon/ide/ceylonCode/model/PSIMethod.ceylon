import com.intellij.psi {
    PsiMethod,
    PsiModifier {
        ...
    },
    PsiType,
    PsiAnnotationMethod,
    PsiParameter
}
import com.intellij.psi.search.searches {
    SuperMethodsSearch
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

class PSIMethod(shared PsiMethod psi)
        extends PSIAnnotatedMirror(psi)
        satisfies MethodMirror {
    
    Boolean classIs(String cls) {
        return psi.containingClass.qualifiedName == cls;
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
            value overloads = doWithLock(
                () => psi.containingClass.findMethodsByName(psi.name, true)
            );
            return overloads.size > 1;
        }
    }
    
    variable Boolean? lazyIsOverloading = null;
    
    shared Boolean isOverloading => lazyIsOverloading else (lazyIsOverloading = computedIsOverloading);
    
    abstract =>
        psi.hasModifierProperty(\iABSTRACT)
        || psi.containingClass.\iinterface;
    
    constructor => psi.constructor;
    
    declaredVoid => psi.returnType == PsiType.\iVOID;
    
    default => if (is PsiAnnotationMethod psi)
               then psi.defaultValue exists
               else false;
    
    defaultAccess => !(public || protected || psi.hasModifierProperty(\iPRIVATE));
    
    enclosingClass => PSIClass(psi.containingClass);
    
    final => psi.hasModifierProperty(\iFINAL);
    
    parameters => mirror<VariableMirror,PsiParameter>(psi.parameterList.parameters, PSIVariable);
    
    protected => psi.hasModifierProperty(\iPROTECTED);
    
    public => psi.hasModifierProperty(\iPUBLIC);
    
    shared actual TypeMirror? returnType
            => if (exists t = psi.returnType) then PSIType(t) else null;
    
    static => psi.hasModifierProperty(\iSTATIC);
    
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
    
    defaultMethod => psi.hasModifierProperty(PsiModifier.\iDEFAULT);   
}