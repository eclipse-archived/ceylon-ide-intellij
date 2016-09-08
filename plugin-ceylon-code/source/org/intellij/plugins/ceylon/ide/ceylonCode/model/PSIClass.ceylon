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
    PsiManager,
    PsiNameIdentifierOwner,
    SmartPsiElementPointer
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.psi.impl.light {
    LightMethodBuilder
}
import com.intellij.psi.impl.source {
    PsiExtensibleClass,
    ClassInnerStuffCache
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

import java.util {
    ArrayList,
    Arrays
}

// TODO investigate why psi.containingFile is sometimes null
shared class PSIClass(SmartPsiElementPointer<PsiClass> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies IdeClassMirror {

    shared PsiClass psi {
        "The PSI element should still exist"
        assert(exists el = psiPointer.element);
        return el;
    }
    
    variable String? cacheKey = null;
    
    Boolean hasAnnotation(Annotations annotation)
        => let (cn = annotation.className)
            concurrencyManager.needReadAccess(() =>
            any {
                if (exists mods = psi.modifierList)
                for (ann in mods.annotations)
                if (exists name = ann.qualifiedName)
                    name == cn
            });

    abstract => PsiUtil.isAbstractClass(psi);
    
    annotationType => psi.annotationType;
    
    anonymous => psi is PsiAnonymousClass;
    
    ceylonToplevelAttribute => !innerClass && hasAnnotation(Annotations.attribute);
    
    ceylonToplevelMethod => !innerClass && hasAnnotation(Annotations.method);
    
    ceylonToplevelObject => !innerClass && hasAnnotation(Annotations.\iobject);
    
    defaultAccess
            => let (private = psi.hasModifierProperty(PsiModifier.private))
                 !(public || protected || private);

    typeParameters
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<TypeParameterMirror>(
                    for (tp in psi.typeParameters)
                        PSITypeParameter(tp)));

    directFields
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<FieldMirror>(
                    for (f in psi.fields)
                    if (!f.hasModifierProperty(PsiModifier.private)) // TODO !f.synthetic?
                        PSIField(pointer(f))));
    
    directInnerClasses
            => concurrencyManager.needReadAccess(()
                => Arrays.asList<ClassMirror>(
                    for (ic in psi.innerClasses)
                        PSIClass(pointer(ic))));
    
    directMethods => concurrencyManager.needReadAccess(() {
            value result = ArrayList<MethodMirror>();
            variable value hasCtor = false;

            for (m in psi.methods) {
                if (m.constructor) {
                    hasCtor = true;
                }
                result.add(PSIMethod(pointer(m)));
            }

            if (psi.enum,
                is PsiExtensibleClass ec = psi) {
                value cache = ClassInnerStuffCache(ec);
                if (exists valueOfMethod = cache.valueOfMethod) {
                    result.add(PSIMethod(pointer(valueOfMethod)));
                }
                if (exists valuesMethod = cache.valuesMethod) {
                    result.add(PSIMethod(pointer(valuesMethod)));
                }
            }

            // unfortunately, IntelliJ does not include implicit default constructors in `psi.methods`
            if (!hasCtor) {
                value builder
                        = LightMethodBuilder(
                            PsiManager.getInstance(psi.project),
                            JavaLanguage.instance,
                            (psi of PsiNameIdentifierOwner).name)
                        .addModifier("public")
                        .setConstructor(true);
                result.add(PSIMethod(pointer(builder)));
            }
            return result;
        });
    
    enclosingClass
            => if (exists outerClass = getContextOfType(psi, javaClass<PsiClass>()))
            then PSIClass(pointer(outerClass))
            else null;
    
    enclosingMethod
            => if (exists outerMeth = getContextOfType(psi, javaClass<PsiMethod>()))
            then PSIMethod(pointer(outerMeth))
            else null;
    
    enum => psi.enum;
    
    final => psi.hasModifierProperty(PsiModifier.final);
    
    flatName => concurrencyManager.needReadAccess(() => psi.qualifiedName else "");
   
    getCacheKey(Module mod) 
            => cacheKey else (cacheKey = getCacheKeyByModule(mod, qualifiedName));
    
    innerClass => psi.containingClass exists || hasAnnotation(Annotations.container);

    \iinterface => psi.\iinterface;
    
    interfaces => concurrencyManager.needReadAccess(()
            => let (supertypes = psi.\iinterface
                    then psi.extendsListTypes
                    else psi.implementsListTypes)
                Arrays.asList<TypeMirror>(
                    for (t in supertypes)
                        PSIType(t)));
    
    javaSource => concurrencyManager.needReadAccess(() => psi.containingFile else null)?.name?.endsWith(".java") else false;
    
    loadedFromSource => javaSource;
    
    localClass => PsiUtil.isLocalClass(psi) || hasAnnotation(Annotations.localContainer);
    
    \ipackage => PSIPackage(psiPointer);
     
    protected => psi.hasModifierProperty(PsiModifier.protected);
    
    public => psi.hasModifierProperty(PsiModifier.public);
    
    qualifiedName => 
            if (is PsiTypeParameter tp = psi)
            then \ipackage.qualifiedName + "." + name
            else (concurrencyManager.needReadAccess(() => psi.qualifiedName else ""));
    
    static => psi.hasModifierProperty(PsiModifier.static);
    
    shared actual TypeMirror? superclass {
        if (psi.\iinterface || qualifiedName == "java.lang.Object") {
            return null;
        }
        return concurrencyManager.needReadAccess(()
            // TODO check that the first element is always the superclass
            => if (exists st = psi.superTypes[0]) then PSIType(st) else null);
    }
    
    fileName => psi.containingFile?.name else "<unknown>";
    
    fullPath => if (exists f = psi.containingFile)
                then let (p = f.virtualFile.path)
                    if (exists i = p.firstInclusion("!/"))
                    then p.spanFrom(i + 2)
                    else "<unknown>"
                else "<unknown>";
    
    isBinary => psi is ClsClassImpl;
    
    isCeylon => hasAnnotation(Annotations.ceylon);
    
}
