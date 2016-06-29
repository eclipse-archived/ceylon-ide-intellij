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
    HierarchicalMethodSignature,
    PsiElement,
    PsiField,
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

class CeyLightToplevelFunction(declaration, project)
        extends LightElement(PsiManager.getInstance(project), CeylonLanguage.instance)
        satisfies PsiClass & CeylonLightElement {

    shared actual Function declaration;
    Project project;

    value mirror = ceylonToJavaMapper.mapDeclaration(declaration).first;
    assert(is JToplevelFunctionMirror mirror);

    allFields => createJavaObjectArray<PsiField>({});
    
    allInnerClasses => createJavaObjectArray<PsiClass>({});

    allMethodsAndTheirSubstitutors
            => Collections.emptyList<Pair<PsiMethod, PsiSubstitutor>>();
    
    annotationType => false;
    
    constructors => createJavaObjectArray<PsiMethod>({});
    
    containingClass => null;
    
    deprecated => declaration.deprecated;
    
    docComment => null;
    
    enum => false;
    
    extendsList => null;
    
    extendsListTypes => createJavaObjectArray<PsiClassType>({});
    
    fields => createJavaObjectArray<PsiField>({});
    
    findFieldByName(String name, Boolean checkBases) => null;
    
    findInnerClassByName(String name, Boolean checkBases) => null;
    
    findMethodBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => null;
    
    findMethodsAndTheirSubstitutorsByName(String name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod, PsiSubstitutor>>();
    
    findMethodsByName(String name, Boolean checkBases)
            => createJavaObjectArray<PsiMethod>({});
    
    findMethodsBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => createJavaObjectArray<PsiMethod>({});
    
    hasModifierProperty(String name)
            => name == "public"
               then declaration.shared
               else false;
    
    hasTypeParameters() => false;
    
    implementsList => null;
    
    implementsListTypes => createJavaObjectArray<PsiClassType>({});
    
    initializers => createJavaObjectArray<PsiClassInitializer>({});
    
    innerClasses => createJavaObjectArray<PsiClass>({});
    
    \iinterface => false;
    
    interfaces => createJavaObjectArray<PsiClass>({});
    
    isInheritor(PsiClass? baseClass, Boolean checkDeep) => false;
    
    isInheritorDeep(PsiClass? baseClass, PsiClass? classToByPass) => false;

    rBrace => null;
    lBrace => null;

    nameIdentifier => null;
    
    qualifiedName => mirror.qualifiedName;

    scope => null;
    
    shared actual PsiElement setName(String? name) {
        throw IncorrectOperationException();
    }
    
    string => "CeyLightToplevelFunction:" + mirror.name;
    
    superClass => null;
    
    superTypes => createJavaObjectArray<PsiClassType>({});
    
    supers => createJavaObjectArray<PsiClass>({
        JavaPsiFacade.getInstance(project)
            .findClass(CommonClassNames.javaLangObject, GlobalSearchScope.allScope(project))
    });
    
    typeParameterList => null;
    
    typeParameters => createJavaObjectArray<PsiTypeParameter>({});
    
    visibleSignatures => Collections.emptyList<HierarchicalMethodSignature>();
    
    name => mirror.name;

    variable ObjectArray<PsiMethod>? lazyMethods = null;

    allMethods
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
            given T satisfies Object
            => (super of UserDataHolderBase).putCopyableUserData(key, val);

    containingFile => CeylonTreeUtil.getDeclaringFile(declaration.unit, project);

    shared actual PsiElement? navigationElement
            => (super of LightElement).navigationElement;
    assign navigationElement
            => (super of LightElement).navigationElement = navigationElement;

    processDeclarations(PsiScopeProcessor processor, ResolveState state,
        PsiElement lastParent, PsiElement place)
            => PsiClassImplUtil.processDeclarationsInClass(this, processor, state, null,
                lastParent, place, PsiUtil.getLanguageLevel(place), false);
}
