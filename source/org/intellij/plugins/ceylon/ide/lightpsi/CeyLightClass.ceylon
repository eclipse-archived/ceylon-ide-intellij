import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Pair,
    Key,
    UserDataHolderBase
}
import com.intellij.pom {
    Navigatable
}
import com.intellij.psi {
    PsiManager,
    PsiTypeParameter,
    PsiClassType,
    PsiClass,
    PsiClassInitializer,
    PsiField,
    PsiMethod,
    PsiSubstitutor,
    PsiElement,
    HierarchicalMethodSignature,
    ResolveState
}
import com.intellij.psi.impl {
    PsiClassImplUtil
}
import com.intellij.psi.impl.light {
    LightElement,
    LightModifierList
}
import com.intellij.psi.impl.source {
    ClassInnerStuffCache,
    PsiExtensibleClass
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.psi.util {
    PsiUtil
}
import com.intellij.util {
    IncorrectOperationException
}
import com.redhat.ceylon.ide.common.model {
    CeylonUnit
}
import com.redhat.ceylon.ide.common.model.asjava {
    AbstractClassMirror,
    ceylonToJavaMapper,
    AbstractMethodMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    TypeDeclaration
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList,
    Collections,
    List
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.lightpsi {
    CeyLightMethod,
    CeyLightTypeParameter
}
import org.intellij.plugins.ceylon.ide.model {
    PSIClass,
    PSIMethod
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonTreeUtil
}
import org.intellij.plugins.ceylon.ide.resolve {
    IdeaNavigation
}


List<String> toJavaModifiers(Declaration decl) {
    value modifiers = ArrayList<String>();
    
    if (decl.formal) {
        modifiers.add("abstract");
    }
    if (decl.shared) {
        modifiers.add("public");
    }
    if (is TypeDeclaration decl, decl.sealed) {
        modifiers.add("final");
    }
    return modifiers;
}

shared class CeyLightClass extends LightElement
        satisfies PsiExtensibleClass & CeylonLightElement {

    AbstractClassMirror mirror;
    variable ClassInnerStuffCache? lazyInnersCache = null;

    shared new(Declaration decl, Project project)
            extends LightElement(PsiManager.getInstance(project), ceylonLanguage) {
        assert (is AbstractClassMirror mir = ceylonToJavaMapper.mapDeclaration(decl).first);
        this.mirror = mir;
    }

    shared new fromMirror(AbstractClassMirror mirror, Project project)
            extends LightElement(PsiManager.getInstance(project), ceylonLanguage) {
        this.mirror = mirror;
    }

    "The declaration must be in a Ceylon unit"
    assert (is CeylonUnit unit = mirror.decl.unit);

    declaration => mirror.decl;

    value modifiers = toJavaModifiers(declaration);

    modifierList => LightModifierList(manager, language, for (m in modifiers) m);

    hasModifierProperty(String name) => name in modifiers;

    docComment => null;

    deprecated => mirror.decl.deprecated;

    hasTypeParameters() => !mirror.typeParameters.empty;

    typeParameterList => null;

    shared actual ObjectArray<PsiTypeParameter> typeParameters {
        value list = mirror.typeParameters;
        value params = ObjectArray<PsiTypeParameter>(list.size());
        variable Integer i = 0;
        for (mirror in list) {
            params[i++] = CeyLightTypeParameter(mirror, manager);
        }
        return params;
    }

    qualifiedName => mirror.qualifiedName;

    \iinterface => mirror.\iinterface;

    annotationType => mirror.annotationType;

    enum => mirror.enum;

    extendsList => null;

    implementsList => null;

    extendsListTypes => PsiClassType.emptyArray;

    implementsListTypes => PsiClassType.emptyArray;

    shared actual PsiClass? superClass {
        if (mirror.superclass exists) {
            // TODO?
            print(mirror.superclass);
        }
        return null;
    }

    interfaces => PsiClass.emptyArray;

    supers => PsiClass.emptyArray;

    shared actual ObjectArray<PsiClassType> superTypes {
        value superTypes = ArrayList<PsiClassType>();
        if (exists sup = mirror.superclass) {
            superTypes.add(createType(sup, project));
        }
        for (intf in mirror.interfaces) {
            superTypes.add(createType(intf, project));
        }
        if (superTypes.size() == 0) {
            return PsiClassType.emptyArray;
        } else {
            return superTypes.toArray(PsiClassType.arrayFactory.create(superTypes.size()));
        }
    }

    value myInnersCache
            => lazyInnersCache else (lazyInnersCache = ClassInnerStuffCache(this));

    fields => myInnersCache.fields;

    methods => myInnersCache.methods;

    constructors => myInnersCache.constructors;

    innerClasses => myInnersCache.innerClasses;

    initializers => PsiClassInitializer.emptyArray;

    allFields => PsiClassImplUtil.getAllFields(this);

    allMethods => PsiClassImplUtil.getAllMethods(this);

    allInnerClasses => PsiClass.emptyArray;

    findFieldByName(String name, Boolean checkBases) => null;

    findMethodBySignature(PsiMethod patternMethod, Boolean checkBases)
            => null;

    findMethodsBySignature(PsiMethod patternMethod, Boolean checkBases) => PsiMethod.emptyArray;

    findMethodsByName(String name, Boolean checkBases) => PsiMethod.emptyArray;

    findMethodsAndTheirSubstitutorsByName(String name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();

    allMethodsAndTheirSubstitutors
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();

    findInnerClassByName(String name, Boolean checkBases) => null;

    lBrace => null;

    rBrace => null;

    nameIdentifier => null;

    scope => null;

    isInheritor(PsiClass baseClass, Boolean checkDeep) => false;

    isInheritorDeep(PsiClass baseClass, PsiClass classToByPass) => false;

    containingClass => null;

    visibleSignatures => Collections.emptyList<HierarchicalMethodSignature>();

    shared actual PsiElement setName(String name) {
        throw IncorrectOperationException("Not supported");
    }

    containingFile => CeylonTreeUtil.getDeclaringFile(mirror.decl.unit, project);

    name => mirror.name;

    string => "CeyLightClass:" + mirror.name;

    ownFields => Collections.emptyList<PsiField>();

    shared actual List<PsiMethod> ownMethods {
        value methods = ArrayList<PsiMethod>();
        for (meth in mirror.directMethods) {
            if (is PSIMethod meth) {
                methods.add(meth.psi);
            } else {
                assert (is AbstractMethodMirror meth);
                methods.add(CeyLightMethod(this, meth, project));
            }
        }
        return methods;
    }

    shared actual List<PsiClass> ownInnerClasses {
        value classes = ArrayList<PsiClass>();
        for (cls in mirror.directInnerClasses) {
            switch (cls)
            case (is AbstractClassMirror) {
                classes.add(fromMirror(cls, project));
            }
            case (is PSIClass) {
                classes.add(cls.psi);
            }
            else {}
        }
        return classes;
    }

    processDeclarations(PsiScopeProcessor processor, ResolveState state,
        PsiElement lastParent, PsiElement place)
            => PsiClassImplUtil.processDeclarationsInClass(this, processor, state, null,
                    lastParent, place, PsiUtil.getLanguageLevel(place), false);

    shared actual void navigate(Boolean requestFocus) {
        if (is Navigatable nav = IdeaNavigation(project).gotoDeclaration(declaration)) {
            nav.navigate(requestFocus);
        }
    }

    // Ambiguous members inherited from multiple parents
    valid => (super of LightElement).valid;

    writable => (super of LightElement).writable;

    shared actual T getCopyableUserData<T>(Key<T> key)
            given T satisfies Object
            => (super of UserDataHolderBase).getCopyableUserData(key);

    shared actual void putCopyableUserData<T>(Key<T> key, T? val)
            given T satisfies Object
            => (super of UserDataHolderBase).putCopyableUserData(key, val);

    shared actual PsiElement navigationElement
            => (super of LightElement).navigationElement;
    assign navigationElement
            => (super of LightElement).navigationElement = navigationElement;
}
