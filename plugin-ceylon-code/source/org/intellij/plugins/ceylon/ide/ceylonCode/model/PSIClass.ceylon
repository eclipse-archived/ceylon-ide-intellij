import ceylon.interop.java {
    javaClass
}

import com.intellij.psi {
    PsiClass,
    PsiModifier,
    PsiAnonymousClass,
    PsiMethod,
    PsiTypeParameter,
    PsiField,
    PsiClassType
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.psi.util {
    PsiUtil,
    PsiTreeUtil {
        getContextOfType
    }
}
import com.redhat.ceylon.ide.common.model.mirror {
    IdeClassMirror
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader {
        getCacheKeyByModule
    }
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    TypeParameterMirror,
    FieldMirror,
    PackageMirror,
    TypeMirror,
    MethodMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.lang {
    ObjectArray
}
import java.util {
    List,
    ArrayList
}

class PSIClass(shared PsiClass psi)
        extends PSIAnnotatedMirror(psi)
        satisfies IdeClassMirror {
    
    variable String? cacheKey = null;
    
    Boolean hasAnnotation(Annotations annotation) {
        return psi.modifierList.annotations.array.coalesced.any(
            (ann) =>ann.qualifiedName == annotation.klazz.canonicalName
        );
    }
    
    shared actual Boolean abstract => PsiUtil.isAbstractClass(psi);
    
    shared actual Boolean annotationType => psi.annotationType;
    
    shared actual Boolean anonymous => psi is PsiAnonymousClass;
    
    shared actual Boolean ceylonToplevelAttribute
            => !innerClass && hasAnnotation(Annotations.attribute);
    
    shared actual Boolean ceylonToplevelMethod
            => !innerClass && hasAnnotation(Annotations.method);
    
    shared actual Boolean ceylonToplevelObject
            => !innerClass && hasAnnotation(Annotations.\iobject);
    
    shared actual Boolean defaultAccess
            => let (private = psi.hasModifierProperty(PsiModifier.\iPRIVATE)) 
                !(public || protected || private);
    
    shared actual List<FieldMirror> directFields
            => mirror<FieldMirror,PsiField>(psi.fields, PSIField);
    
    shared actual List<ClassMirror> directInnerClasses
            => mirror<ClassMirror,PsiClass>(psi.innerClasses, PSIClass);
    
    shared actual List<MethodMirror> directMethods {
        return doWithLock(() {
            value result = ArrayList<MethodMirror>();
            psi.methods.array.coalesced.each((m) {
                result.add(PSIMethod(m));
            });
            return result;
        });
    }
    
    shared actual ClassMirror? enclosingClass =>
            if (exists outerClass = getContextOfType(psi, javaClass<PsiClass>()))
            then PSIClass(outerClass)
            else null;
    
    shared actual MethodMirror? enclosingMethod =>
            if (exists outerMeth = getContextOfType(psi, javaClass<PsiMethod>()))
            then PSIMethod(outerMeth)
            else null;
    
    shared actual Boolean enum => psi.enum;
    
    shared actual Boolean final
            => psi.hasModifierProperty(PsiModifier.\iFINAL);
    
    shared actual String flatName => psi.qualifiedName else "";
   
    shared actual String getCacheKey(Module mod) 
            => cacheKey else (cacheKey = getCacheKeyByModule(mod, qualifiedName));
    
    shared actual Boolean innerClass => PsiUtil.isInnerClass(psi);
    
    shared actual Boolean \iinterface => psi.\iinterface;
    
    shared actual List<TypeMirror> interfaces
            => if (psi.\iinterface)
               then mirror<TypeMirror,PsiClassType>(psi.extendsListTypes, PSIType)
               else mirror<TypeMirror,PsiClassType>(psi.implementsListTypes, PSIType);
    
    shared actual Boolean javaSource
            => psi.containingFile.name.endsWith(".java");
    
    shared actual Boolean loadedFromSource => javaSource;
    
    shared actual Boolean localClass
            => PsiUtil.isLocalClass(psi)
               || hasAnnotation(Annotations.localContainer);
    
    shared actual PackageMirror \ipackage => PSIPackage(psi);
     
    shared actual Boolean protected
             => psi.hasModifierProperty(PsiModifier.\iPROTECTED);
    
    shared actual Boolean public
            => psi.hasModifierProperty(PsiModifier.\iPUBLIC);
    
    shared actual String qualifiedName => 
            if (is PsiTypeParameter psi)
            then \ipackage.qualifiedName + "." + name
            else (psi.qualifiedName else "");
    
    shared actual Boolean static
            => psi.hasModifierProperty(PsiModifier.\iSTATIC);
    
    shared actual TypeMirror? superclass {
        if (psi.\iinterface || qualifiedName == "java.lang.Object") {
            return null;
        }
        
        return doWithLock(() {
            value superTypes = psi.superTypes.array.coalesced;
            
            // TODO check that the first element is always the superclass
            return if (exists st = superTypes.first) then PSIType(st) else null; 
        });
    }
    
    shared actual List<TypeParameterMirror> typeParameters
            => mirror<TypeParameterMirror,PsiTypeParameter>
                (psi.typeParameters, PSITypeParameter);
    
    shared actual String fileName => psi.containingFile.name;
    
    shared actual String fullPath => 
            let (p = psi.containingFile.virtualFile.canonicalPath)
            if (exists i = p.firstInclusion("!/"))
            then p.spanFrom(i + 2)
            else p;
    
    shared actual Boolean isBinary => psi is ClsClassImpl;
    
    shared actual Boolean isCeylon => hasAnnotation(Annotations.ceylon);
    
}

List<Out> mirror<Out,In>(ObjectArray<In> array, Out transform(In val)) {
    value result = ArrayList<Out>(array.size);
    
    array.array.coalesced.each(
        (v) => result.add(transform(v))
    );
    
    return result;
}