import com.intellij.openapi.util {
    Key,
    Pair
}
import com.intellij.psi {
    PsiTypeParameter,
    PsiMethod,
    PsiAnnotation,
    PsiManager,
    PsiClassInitializer,
    PsiReference,
    HierarchicalMethodSignature,
    PsiClass,
    PsiField,
    PsiClassType,
    ResolveState,
    PsiSubstitutor,
    PsiElementVisitor,
    PsiElement
}
import com.intellij.psi.impl.light {
    LightEmptyImplementsList
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.util {
    IncorrectOperationException
}
import com.redhat.ceylon.model.loader.mirror {
    TypeParameterMirror
}

import java.lang {
    CharSequence,
    CharArray,
    UnsupportedOperationException,
    overloaded
}
import java.util {
    Collections
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import com.intellij.psi.search {
    GlobalSearchScope
}

class CeyLightTypeParameter(TypeParameterMirror mirror, shared actual PsiManager manager)
        satisfies PsiTypeParameter {
    
    shared actual void accept(PsiElementVisitor visitor) {}
    
    shared actual void acceptChildren(PsiElementVisitor visitor) {}
    
    add(PsiElement element) => null;
    
    addAfter(PsiElement element, PsiElement? anchor)
            => null;
    
    shared actual PsiAnnotation addAnnotation(String qualifiedName) {
        throw UnsupportedOperationException();
    }
    
    addBefore(PsiElement element, PsiElement? anchor) => null;
    
    addRange(PsiElement first, PsiElement? last) => null;
    
    addRangeAfter(PsiElement first, PsiElement last, PsiElement? anchor) => null;
    
    addRangeBefore(PsiElement first, PsiElement last, PsiElement? anchor) => null;
    
    allFields => PsiField.emptyArray;
    
    allInnerClasses => PsiClass.emptyArray;
    
    allMethods => PsiMethod.emptyArray;
    
    allMethodsAndTheirSubstitutors
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();
    
    annotationType => false;
    
    annotations => PsiAnnotation.emptyArray;
    
    applicableAnnotations => PsiAnnotation.emptyArray;
    
    canNavigate() => false;
    
    canNavigateToSource() => false;
    
    shared actual void checkAdd(PsiElement element) {}
    
    shared actual void checkDelete() {}
    
    children => PsiElement.emptyArray;
    
    constructors => PsiMethod.emptyArray;
    
    containingClass => null;
    
    containingFile => null;
    
    context => null;
    
    copy() => null;
    
    shared actual void delete() {}
    
    shared actual void deleteChildRange(PsiElement? first, PsiElement? last) {}
    
    deprecated => false;
    
    docComment => null;
    
    enum => false;
    
    extendsList => LightEmptyImplementsList(manager);
    
    extendsListTypes => PsiClassType.emptyArray;
    
    fields => PsiField.emptyArray;
    
    findAnnotation(String qualifiedName) => null;
    
    findElementAt(Integer offset) => null;
    
    findFieldByName(String? name, Boolean checkBases) => null;
    
    findInnerClassByName(String? name, Boolean checkBases) => null;
    
    findMethodBySignature(PsiMethod? patternMethod, Boolean checkBases) => null;
    
    findMethodsAndTheirSubstitutorsByName(String? name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();
    
    findMethodsByName(String? name, Boolean checkBases) => PsiMethod.emptyArray;
    
    findReferenceAt(Integer offset) => null;
    
    firstChild => null;
    
    shared actual T? getCopyableUserData<T>(Key<T>? key)
            given T satisfies Object => null;
    
    getIcon(Integer flags) => null;
    
    shared actual T? getUserData<T>(Key<T> key)
            given T satisfies Object => null;
    
    hasModifierProperty(String name) => false;
    
    hasTypeParameters() => false;
    
    implementsList => null;
    
    implementsListTypes => PsiClassType.emptyArray;
    
    index => 0;
    
    initializers => PsiClassInitializer.emptyArray;
    
    innerClasses => PsiClass.emptyArray;
    
    \iinterface => false;
    
    interfaces => PsiClass.emptyArray;
    
    isEquivalentTo(PsiElement another) => false;
    
    isInheritor(PsiClass baseClass, Boolean checkDeep) => false;
    
    isInheritorDeep(PsiClass? baseClass, PsiClass? classToByPass) => false;
    
    lBrace => null;
    
    language => ceylonLanguage;
    
    lastChild => null;
    
    methods => PsiMethod.emptyArray;
    
    modifierList => null;
    
    shared actual String name => mirror.name;
    
    nameIdentifier => null;
    
    shared actual void navigate(Boolean requestFocus) {}
    
    shared actual PsiElement? navigationElement => null;
    
    nextSibling => null;
    
    node => null;
    
    originalElement => null;
    
    owner => null;
    
    parent => null;
    
    physical => true;
    
    presentation => null;
    
    prevSibling => null;
    
    processDeclarations(PsiScopeProcessor processor, ResolveState state,
        PsiElement? lastParent, PsiElement place) => false;
    
    project => manager.project;
    
    shared actual void putCopyableUserData<T>(Key<T>? key, T? val)
            given T satisfies Object {}
    
    shared actual void putUserData<T>(Key<T> key, T? val)
            given T satisfies Object {}
    
    qualifiedName => mirror.name;
    
    rBrace => null;
    
    reference => null;
    
    references => PsiReference.emptyArray;
    
    replace(PsiElement newElement) => null;
    
    resolveScope => GlobalSearchScope.emptyScope;
    
    scope => null;
    
    shared actual PsiElement setName(String name) {
        throw IncorrectOperationException("Not supported");
    }
    
    startOffsetInParent => 0;
    
    string => "CeyLightTypeParameter";
    
    superClass => null;
    
    superTypes => PsiClassType.emptyArray;
    
    supers => PsiClass.emptyArray;
    
    text => null;
    
    textContains(Character c) => false;
    
    textLength => 0;

    overloaded
    shared actual Boolean textMatches(CharSequence text) => false;

    overloaded
    shared actual Boolean textMatches(PsiElement element) => false;
    
    textOffset => 0;
    
    textRange => null;
    
    textToCharArray() => CharArray(0);
    
    typeParameterList => null;
    
    typeParameters => PsiTypeParameter.emptyArray;
    
    useScope => GlobalSearchScope.emptyScope;

    shared actual Boolean writable => false;

    shared actual Boolean valid => true;
    
    visibleSignatures => Collections.emptyList<HierarchicalMethodSignature>();
    
    findMethodsBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => PsiMethod.emptyArray;
    
}