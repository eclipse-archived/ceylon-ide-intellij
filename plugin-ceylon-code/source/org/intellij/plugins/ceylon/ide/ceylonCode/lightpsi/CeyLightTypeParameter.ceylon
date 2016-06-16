import com.intellij.psi {
    PsiTypeParameter,
    PsiMethod,
    PsiTypeParameterListOwner,
    PsiAnnotation,
    PsiModifierList,
    PsiManager,
    PsiClassInitializer,
    PsiReferenceList,
    PsiReference,
    HierarchicalMethodSignature,
    PsiClass,
    PsiField,
    PsiClassType,
    PsiIdentifier,
    ResolveState,
    PsiSubstitutor,
    PsiFile,
    PsiElementVisitor,
    PsiTypeParameterList,
    PsiElement
}
import com.intellij.psi.javadoc {
    PsiDocComment
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.openapi.project {
    Project
}
import java.util {
    List,
    Collection,
    Collections
}
import com.intellij.lang {
    Language,
    ASTNode
}
import com.intellij.navigation {
    ItemPresentation
}
import com.intellij.psi.search {
    GlobalSearchScope,
    SearchScope
}
import java.lang {
    CharSequence,
    ObjectArray,
    CharArray
}
import com.intellij.openapi.util {
    TextRange,
    Key,
    Pair
}
import javax.swing {
    Icon
}
import com.redhat.ceylon.model.loader.mirror {
    TypeParameterMirror
}
import com.intellij.util {
    IncorrectOperationException
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import com.intellij.psi.impl.light {
    LightEmptyImplementsList
}

class CeyLightTypeParameter(TypeParameterMirror mirror, shared actual PsiManager manager)
        satisfies PsiTypeParameter {
    
    shared actual void accept(PsiElementVisitor? visitor) {}
    
    shared actual void acceptChildren(PsiElementVisitor? visitor) {}
    
    shared actual PsiElement? add(PsiElement? element) => null;
    
    shared actual PsiElement? addAfter(PsiElement? element, PsiElement? anchor)
            => null;
    
    shared actual PsiAnnotation? addAnnotation(String? qualifiedName) => null;
    
    shared actual PsiElement? addBefore(PsiElement? element, PsiElement? anchor) => null;
    
    shared actual PsiElement? addRange(PsiElement? first, PsiElement? last) => null;
    
    shared actual PsiElement? addRangeAfter(PsiElement? first, PsiElement? last, PsiElement? anchor) => null;
    
    shared actual PsiElement? addRangeBefore(PsiElement? first, PsiElement? last, PsiElement? anchor) => null;
    
    shared actual ObjectArray<PsiField> allFields => PsiField.emptyArray;
    
    shared actual ObjectArray<PsiClass> allInnerClasses => PsiClass.emptyArray;
    
    shared actual ObjectArray<PsiMethod> allMethods => PsiMethod.emptyArray;
    
    shared actual List<Pair<PsiMethod,PsiSubstitutor>> allMethodsAndTheirSubstitutors
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();
    
    shared actual Boolean annotationType => false;
    
    shared actual ObjectArray<PsiAnnotation> annotations => PsiAnnotation.emptyArray;
    
    shared actual ObjectArray<PsiAnnotation> applicableAnnotations => PsiAnnotation.emptyArray;
    
    shared actual Boolean canNavigate() => false;
    
    shared actual Boolean canNavigateToSource() => false;
    
    shared actual void checkAdd(PsiElement? element) {}
    
    shared actual void checkDelete() {}
    
    shared actual ObjectArray<PsiElement> children => PsiElement.emptyArray;
    
    shared actual ObjectArray<PsiMethod> constructors => PsiMethod.emptyArray;
    
    shared actual PsiClass? containingClass => null;
    
    shared actual PsiFile? containingFile => null;
    
    shared actual PsiElement? context => null;
    
    shared actual PsiElement? copy() => null;
    
    shared actual void delete() {}
    
    shared actual void deleteChildRange(PsiElement? first, PsiElement? last) {}
    
    shared actual Boolean deprecated => false;
    
    shared actual PsiDocComment? docComment => null;
    
    shared actual Boolean enum => false;
    
    extendsList => LightEmptyImplementsList(manager);
    
    shared actual ObjectArray<PsiClassType> extendsListTypes => PsiClassType.emptyArray;
    
    shared actual ObjectArray<PsiField> fields => PsiField.emptyArray;
    
    shared actual PsiAnnotation? findAnnotation(String? qualifiedName) => null;
    
    shared actual PsiElement? findElementAt(Integer offset) => null;
    
    shared actual PsiField? findFieldByName(String? name, Boolean checkBases) => null;
    
    shared actual PsiClass? findInnerClassByName(String? name, Boolean checkBases) => null;
    
    shared actual PsiMethod? findMethodBySignature(PsiMethod? patternMethod, Boolean checkBases) => null;
    
    shared actual List<Pair<PsiMethod,PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(String? name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();
    
    shared actual ObjectArray<PsiMethod> findMethodsByName(String? name, Boolean checkBases) => PsiMethod.emptyArray;
    
    shared actual PsiReference? findReferenceAt(Integer offset) => null;
    
    shared actual PsiElement? firstChild => null;
    
    shared actual T? getCopyableUserData<T>(Key<T>? key)
            given T satisfies Object => null;
    
    shared actual Icon? getIcon(Integer flags) => null;
    
    shared actual T? getUserData<T>(Key<T>? key)
            given T satisfies Object => null;
    
    shared actual Boolean hasModifierProperty(String? name) => false;
    
    shared actual Boolean hasTypeParameters() => false;
    
    shared actual PsiReferenceList? implementsList => null;
    
    shared actual ObjectArray<PsiClassType> implementsListTypes => PsiClassType.emptyArray;
    
    shared actual Integer index => 0;
    
    shared actual ObjectArray<PsiClassInitializer> initializers => PsiClassInitializer.emptyArray;
    
    shared actual ObjectArray<PsiClass> innerClasses => PsiClass.emptyArray;
    
    shared actual Boolean \iinterface => false;
    
    shared actual ObjectArray<PsiClass> interfaces => PsiClass.emptyArray;
    
    shared actual Boolean isEquivalentTo(PsiElement? another) => false;
    
    shared actual Boolean isInheritor(PsiClass? baseClass, Boolean checkDeep) => false;
    
    shared actual Boolean isInheritorDeep(PsiClass? baseClass, PsiClass? classToByPass) => false;
    
    shared actual PsiElement? lBrace => null;
    
    shared actual Language language => CeylonLanguage.instance;
    
    shared actual PsiElement? lastChild => null;
    
    shared actual ObjectArray<PsiMethod> methods => PsiMethod.emptyArray;
    
    shared actual PsiModifierList? modifierList => null;
    
    shared actual String name => mirror.name;
    
    shared actual PsiIdentifier? nameIdentifier => null;
    
    shared actual void navigate(Boolean requestFocus) {}
    
    shared actual PsiElement? navigationElement => null;
    
    shared actual PsiElement? nextSibling => null;
    
    shared actual ASTNode? node => null;
    
    shared actual PsiElement? originalElement => null;
    
    shared actual PsiTypeParameterListOwner? owner => null;
    
    shared actual PsiElement? parent => null;
    
    shared actual Boolean physical => true;
    
    shared actual ItemPresentation? presentation => null;
    
    shared actual PsiElement? prevSibling => null;
    
    shared actual Boolean processDeclarations(PsiScopeProcessor? processor, ResolveState? state, 
        PsiElement? lastParent, PsiElement? place) => false;
    
    shared actual Project project => manager.project;
    
    shared actual void putCopyableUserData<T>(Key<T>? key, T? \ivalue)
            given T satisfies Object {}
    
    shared actual void putUserData<T>(Key<T>? key, T? \ivalue)
            given T satisfies Object {}
    
    shared actual String qualifiedName => mirror.name;
    
    shared actual PsiElement? rBrace => null;
    
    shared actual PsiReference? reference => null;
    
    shared actual ObjectArray<PsiReference> references => PsiReference.emptyArray;
    
    shared actual PsiElement? replace(PsiElement? newElement) => null;
    
    shared actual GlobalSearchScope? resolveScope => null;
    
    shared actual PsiElement? scope => null;
    
    shared actual PsiElement setName(String? name) {
        throw IncorrectOperationException("Not supported");
    }
    
    shared actual Integer startOffsetInParent => 0;
    
    shared actual String string => "CeyLightTypeParameter";
    
    shared actual PsiClass? superClass => null;
    
    shared actual ObjectArray<PsiClassType> superTypes => PsiClassType.emptyArray;
    
    shared actual ObjectArray<PsiClass> supers => PsiClass.emptyArray;
    
    shared actual String? text => null;
    
    shared actual Boolean textContains(Character c) => false;
    
    shared actual Integer textLength => 0;
    
    shared actual Boolean textMatches(CharSequence? text) => false;
    
    shared actual Boolean textMatches(PsiElement? element) => false;
    
    shared actual Integer textOffset => 0;
    
    shared actual TextRange? textRange => null;
    
    shared actual CharArray textToCharArray() => CharArray(0);
    
    shared actual PsiTypeParameterList? typeParameterList => null;
    
    typeParameters => PsiTypeParameter.emptyArray;
    
    shared actual SearchScope? useScope => null;
    
    shared actual Boolean valid => true;
    
    shared actual Collection<HierarchicalMethodSignature> visibleSignatures
            => Collections.emptyList<HierarchicalMethodSignature>();
    
    shared actual Boolean writable => false;
    
    shared actual ObjectArray<PsiMethod> findMethodsBySignature(PsiMethod? patternMethod, Boolean checkBases)
            => PsiMethod.emptyArray;
    
}