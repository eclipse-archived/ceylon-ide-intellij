import ceylon.interop.java {
    javaClass
}

import com.intellij.lang.java {
    JavaLanguage
}
import com.intellij.psi {
    PsiClass,
    PsiModifier,
    PsiAnonymousClass,
    PsiMethod,
    PsiTypeParameter,
    PsiField,
    PsiClassType,
    PsiManager,
    PsiNameIdentifierOwner
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.psi.impl.light {
    LightMethodBuilder
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

// TODO investigate why psi.containingFile is sometimes null
shared class PSIClass(shared PsiClass psi)
        extends PSIAnnotatedMirror(psi)
        satisfies IdeClassMirror {
    
    variable String? cacheKey = null;
    
    Boolean hasAnnotation(Annotations annotation)
        => doWithLock(()
            => psi.modifierList.annotations.array.coalesced.any(
                (ann) =>ann.qualifiedName == annotation.klazz.canonicalName
            ));

    abstract => PsiUtil.isAbstractClass(psi);
    
    annotationType => psi.annotationType;
    
    anonymous => psi is PsiAnonymousClass;
    
    ceylonToplevelAttribute => !innerClass && hasAnnotation(Annotations.attribute);
    
    ceylonToplevelMethod => !innerClass && hasAnnotation(Annotations.method);
    
    ceylonToplevelObject => !innerClass && hasAnnotation(Annotations.\iobject);
    
    defaultAccess => let (private = psi.hasModifierProperty(PsiModifier.\iPRIVATE)) 
                     !(public || protected || private);
    
    directFields => doWithLock(() =>
        mirror<FieldMirror,PsiField>(
            psi.fields.iterable.coalesced.filter(
                (f) => !f.hasModifierProperty(PsiModifier.\iPRIVATE) // TODO !f.synthetic?
            ),
            PSIField
        )
    );
    
    directInnerClasses => doWithLock(() =>
        mirror<ClassMirror,PsiClass>(psi.innerClasses, PSIClass)
    );
    
    directMethods => doWithLock(() {
            value result = ArrayList<MethodMirror>();
            variable value hasCtor = false;

            psi.methods.array.coalesced.each((m) {
                if (m.constructor) {
                    hasCtor = true;
                }
                result.add(PSIMethod(m));
            });

            // unfortunately, IntelliJ does not include implicit default constructors in `psi.methods`
            if (!hasCtor) {
                value builder = LightMethodBuilder(PsiManager.getInstance(psi.project),
                    JavaLanguage.\iINSTANCE,
                    (psi of PsiNameIdentifierOwner).name
                ).addModifier("public").setConstructor(true);
                result.add(PSIMethod(builder));
            }
            return result;
        });
    
    shared actual ClassMirror? enclosingClass =>
            if (exists outerClass = getContextOfType(psi, javaClass<PsiClass>()))
            then PSIClass(outerClass)
            else null;
    
    shared actual MethodMirror? enclosingMethod =>
            if (exists outerMeth = getContextOfType(psi, javaClass<PsiMethod>()))
            then PSIMethod(outerMeth)
            else null;
    
    enum => psi.enum;
    
    final => psi.hasModifierProperty(PsiModifier.\iFINAL);
    
    flatName => doWithLock(() => psi.qualifiedName else "");
   
    getCacheKey(Module mod) 
            => cacheKey else (cacheKey = getCacheKeyByModule(mod, qualifiedName));
    
    innerClass => psi.containingClass exists || hasAnnotation(Annotations.container);

    \iinterface => psi.\iinterface;
    
    interfaces => doWithLock(() =>
        if (psi.\iinterface)
        then mirror<TypeMirror,PsiClassType>(psi.extendsListTypes, PSIType)
        else mirror<TypeMirror,PsiClassType>(psi.implementsListTypes, PSIType)
    );
    
    javaSource => psi.containingFile?.name?.endsWith(".java") else false;
    
    loadedFromSource => javaSource;
    
    localClass => PsiUtil.isLocalClass(psi) || hasAnnotation(Annotations.localContainer);
    
    \ipackage => PSIPackage(psi);
     
    protected => psi.hasModifierProperty(PsiModifier.\iPROTECTED);
    
    public => psi.hasModifierProperty(PsiModifier.\iPUBLIC);
    
    qualifiedName => 
            if (is PsiTypeParameter psi)
            then \ipackage.qualifiedName + "." + name
            else (doWithLock(() => psi.qualifiedName else ""));
    
    static => psi.hasModifierProperty(PsiModifier.\iSTATIC);
    
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
    
    typeParameters => doWithLock(() =>
        mirror<TypeParameterMirror,PsiTypeParameter>(psi.typeParameters, PSITypeParameter)
    );
    
    fileName => psi.containingFile?.name else "<unknown>";
    
    fullPath => if (exists f = psi.containingFile)
                then let (p = f.virtualFile.canonicalPath)
                    if (exists i = p.firstInclusion("!/"))
                    then p.spanFrom(i + 2)
                    else p
                else "<unknown>";
    
    isBinary => psi is ClsClassImpl;
    
    isCeylon => hasAnnotation(Annotations.ceylon);
    
}

List<Out> mirror<Out,In>(ObjectArray<In>|{In*} array, Out transform(In val)) given In satisfies Object {
    value it = if (is ObjectArray<In> array) then array.iterable.coalesced else array;
    value result = ArrayList<Out>(it.size);
    
    it.coalesced.each(
        (v) => result.add(transform(v))
    );
    
    return result;
}