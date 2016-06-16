import ceylon.interop.java {
    createJavaObjectArray,
    CeylonIterable
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Key,
    Pair,
    UserDataHolderBase
}
import com.intellij.psi {
    PsiClass,
    PsiManager,
    PsiSubstitutor,
    PsiMethod,
    PsiReferenceList,
    HierarchicalMethodSignature,
    PsiTypeParameterList,
    PsiElement,
    PsiField,
    PsiIdentifier,
    PsiTypeParameter,
    PsiClassType,
    PsiClassInitializer,
    ResolveState,
    JavaPsiFacade,
    CommonClassNames
}
import com.intellij.psi.impl {
    PsiClassImplUtil
}
import com.intellij.psi.impl.light {
    LightElement,
    LightModifierList
}
import com.intellij.psi.javadoc {
    PsiDocComment
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.psi.util {
    PsiUtil
}
import com.intellij.util {
    IncorrectOperationException
}
import com.redhat.ceylon.ide.common.model.asjava {
    ceylonToJavaMapper,
    JToplevelFunctionMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Function
}

import java.lang {
    ObjectArray
}
import java.util {
    Collections
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTreeUtil
}

class CeyLightToplevelFunction extends LightElement satisfies PsiClass & CeylonLightElement {
    shared actual Function declaration;
    Project project;
    JToplevelFunctionMirror mirror;

    shared new (Function declaration, Project project)
            extends LightElement(PsiManager.getInstance(project), CeylonLanguage.instance) {
        this.declaration = declaration;
        this.project = project;

        assert(is JToplevelFunctionMirror m = ceylonToJavaMapper.mapDeclaration(declaration).first);
        mirror = m;
    }
    
    allFields => createJavaObjectArray<PsiField>({});
    
    allInnerClasses => createJavaObjectArray<PsiClass>({});

    allMethodsAndTheirSubstitutors => Collections.emptyList<Pair<PsiMethod, PsiSubstitutor>>();
    
    annotationType => false;
    
    constructors => createJavaObjectArray<PsiMethod>({});
    
    shared actual PsiClass? containingClass => null;
    
    deprecated => declaration.deprecated;
    
    shared actual PsiDocComment? docComment => null;
    
    enum => false;
    
    shared actual PsiReferenceList? extendsList => null;
    
    extendsListTypes => createJavaObjectArray<PsiClassType>({});
    
    fields => createJavaObjectArray<PsiField>({});
    
    shared actual PsiField? findFieldByName(String name, Boolean checkBases) => null;
    
    shared actual PsiClass? findInnerClassByName(String name, Boolean checkBases) => null;
    
    shared actual PsiMethod? findMethodBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => null;
    
    findMethodsAndTheirSubstitutorsByName(String name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod, PsiSubstitutor>>();
    
    findMethodsByName(String name, Boolean checkBases) => createJavaObjectArray<PsiMethod>({});
    
    findMethodsBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => createJavaObjectArray<PsiMethod>({});
    
    hasModifierProperty(String name)
            => name == "public"
               then declaration.shared
               else false;
    
    hasTypeParameters() => false;
    
    shared actual PsiReferenceList? implementsList => null;
    
    implementsListTypes => createJavaObjectArray<PsiClassType>({});
    
    initializers => createJavaObjectArray<PsiClassInitializer>({});
    
    innerClasses => createJavaObjectArray<PsiClass>({});
    
    \iinterface => false;
    
    interfaces => createJavaObjectArray<PsiClass>({});
    
    isInheritor(PsiClass? baseClass, Boolean checkDeep) => false;
    
    isInheritorDeep(PsiClass? baseClass, PsiClass? classToByPass) => false;
    
    shared actual PsiElement? lBrace => null;

    shared actual PsiIdentifier? nameIdentifier => null;
    
    qualifiedName => mirror.qualifiedName;
    
    shared actual PsiElement? rBrace => null;
    
    shared actual PsiElement? scope => null;
    
    shared actual PsiElement setName(String? name) {
        throw IncorrectOperationException();
    }
    
    shared actual String string => "CeyLightToplevelFunction:" + mirror.name;
    
    shared actual PsiClass? superClass => null;
    
    superTypes => createJavaObjectArray<PsiClassType>({});
    
    supers => createJavaObjectArray<PsiClass>({
        JavaPsiFacade.getInstance(project).findClass(CommonClassNames.javaLangObject, GlobalSearchScope.allScope(project))
    });
    
    shared actual PsiTypeParameterList? typeParameterList => null;
    
    typeParameters => createJavaObjectArray<PsiTypeParameter>({});
    
    visibleSignatures => Collections.emptyList<HierarchicalMethodSignature>();
    
    name => mirror.name;

    variable ObjectArray<PsiMethod>? lazyMethods = null;
    shared actual ObjectArray<PsiMethod> allMethods
            => lazyMethods else (lazyMethods = createJavaObjectArray(
                    CeylonIterable(mirror.directMethods)
                        .map((m) => CeyLightMethod(this, m, project))
                ));

    methods => allMethods;

    modifierList => LightModifierList(manager, language, "public");

    valid => (super of LightElement).valid;

    writable => (super of LightElement).writable;

    shared actual T getCopyableUserData<T>(Key<T>? key)
            given T satisfies Object
            => (super of UserDataHolderBase).getCopyableUserData(key);

    shared actual void putCopyableUserData<T>(Key<T>? key, T? val)
            given T satisfies Object {
        (super of UserDataHolderBase).putCopyableUserData(key, val);
    }

    containingFile => CeylonTreeUtil.getDeclaringFile(declaration.unit, project);

    shared actual PsiElement? navigationElement => (super of LightElement).navigationElement;
    assign navigationElement {
        (super of LightElement).navigationElement = navigationElement;
    }

    shared actual Boolean processDeclarations(PsiScopeProcessor processor, ResolveState state,
        PsiElement lastParent, PsiElement place) {

        return PsiClassImplUtil.processDeclarationsInClass(this, processor, state, null,
            lastParent, place, PsiUtil.getLanguageLevel(place), false);
    }
}
