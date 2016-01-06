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
    ClassMirror,
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
    
    shared Boolean isOverriding;
    
    if (classIs("ceylon.language.Identifiable"),
        ["equals", "hashCode"].contains(psi.name)) {
        
        isOverriding = true;
    } else if (classIs("ceylon.language.Object"),
        ["equals", "hashCode", "toString"].contains(psi.name)) {
        
        isOverriding = false;
    } else {
        isOverriding = SuperMethodsSearch.search(psi, null, true, false)
                .findFirst() exists;
    }

    shared Boolean isOverloading;
    
    if (classIs("ceylon.language.Exception")) {
        isOverloading = false;
    } else {
        value overloads = psi.containingClass.findMethodsByName(psi.name, true);
        isOverloading = overloads.size > 1;
    }
    
    shared actual Boolean abstract =>
        psi.hasModifierProperty(\iABSTRACT)
        || psi.containingClass.\iinterface;
    
    shared actual Boolean constructor => psi.constructor;
    
    shared actual Boolean declaredVoid => psi.returnType == PsiType.\iVOID;
    
    shared actual Boolean default
            => if (is PsiAnnotationMethod psi)
               then psi.defaultValue exists
               else false;
    
    shared actual Boolean defaultAccess
            => !(public || protected || psi.hasModifierProperty(\iPRIVATE));
    
    shared actual ClassMirror enclosingClass => PSIClass(psi.containingClass);
    
    shared actual Boolean final => psi.hasModifierProperty(\iFINAL);
    
    shared actual List<VariableMirror> parameters
            => mirror<VariableMirror,PsiParameter>
                (psi.parameterList.parameters, PSIVariable);
    
    shared actual Boolean protected => psi.hasModifierProperty(\iPROTECTED);
    
    shared actual Boolean public => psi.hasModifierProperty(\iPUBLIC);
    
    shared actual TypeMirror? returnType
            => if (exists t = psi.returnType) then PSIType(t) else null;
    
    shared actual Boolean static => psi.hasModifierProperty(\iSTATIC);
    
    shared actual Boolean staticInit => false;
    
    shared actual List<TypeParameterMirror> typeParameters {
        value result = ArrayList<TypeParameterMirror>();
         
        psi.typeParameters.array.coalesced.each(
            (tp) => result.add(PSITypeParameter(tp))
        );
        
        return result;
    }
    
    shared actual Boolean variadic => psi.varArgs;

    string => "PSIMethod[``name``]";
    
}