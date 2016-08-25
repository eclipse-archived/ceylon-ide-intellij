import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.util {
    Key,
    Pair
}
import com.intellij.psi {
    ...
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import java.lang {
    ObjectArray,
    CharArray,
    CharSequence
}
import java.util {
    ArrayList,
    Collections,
    List
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}

"Only used to navigate from a decompiled class to a Ceylon source file.
 From the decompiled file, we'll open the source file, then try to locate a PsiClass with
 the same name as the .class file."
shared class NavigationPsiClass(CeylonPsi.StatementOrArgumentPsi decl) satisfies PsiSyntheticClass {

    shared actual ObjectArray<PsiClass> innerClasses {
        if (is CeylonPsi.ClassOrInterfacePsi decl) {
            if (exists body = PsiTreeUtil.findChildOfType(decl, javaClass<CeylonPsi.BodyPsi>())) {
                value children = PsiTreeUtil.findChildrenOfType(body, javaClass<CeylonPsi.ClassOrInterfacePsi>());
                value inners = ArrayList<PsiClass>(children.size());
                for (child in children) {
                    inners.add(NavigationPsiClass(child));
                }
                return inners.toArray(ObjectArray<PsiClass>(children.size()));
            }
        }
        return PsiClass.emptyArray;
    }

    variable List<PsiMethod>? _methods = null;

    shared actual ObjectArray<PsiMethod> findMethodsByName(String name, Boolean checkBases) {
        List<PsiMethod> allMethods = wrapMethods();
        List<PsiMethod> matching = ArrayList<PsiMethod>();
        for (PsiMethod method in allMethods) {
            if (method.name.equals(name)) {
                matching.add(method);
            }
        }
        return matching.toArray(ObjectArray<PsiMethod>(matching.size()));
    }

    List<PsiMethod> wrapMethods() {
        if (!exists m = _methods) {
            value meths = ArrayList<PsiMethod>();
            _methods = meths;

            if (exists body = PsiTreeUtil.findChildOfType(decl, javaClass<CeylonPsi.BodyPsi>())) {
                for (PsiElement child in body.children) {
                    if (is CeylonPsi.AnyMethodPsi child) {
                        value ceylonNode = child.ceylonNode;
                        value paramList = ceylonNode.parameterLists.get(0);

                        // We need to generate extra PsiMethods for default arguments
                        variable value idx = 0;
                        variable value firstDefaultArgPosition = -1;

                        for (p in paramList.parameters) {
                            if (is Tree.ParameterDeclaration p) {
                                if (is Tree.AttributeDeclaration attr = p.typedDeclaration,
                                    attr.specifierOrInitializerExpression exists) {

                                    firstDefaultArgPosition = idx;
                                    break ;
                                } else if (is Tree.MethodDeclaration meth = p.typedDeclaration,
                                    meth.specifierExpression exists) {

                                    firstDefaultArgPosition = idx;
                                    break ;
                                }
                            }
                            idx++;
                        }

                        meths.add(NavigationPsiMethod(child));

                        if (firstDefaultArgPosition > -1) {
                            for (i in firstDefaultArgPosition..paramList.parameters.size() - 1) {
                                meths.add(NavigationPsiMethod.forDefaultArgs(child, i));
                            }
                        }
                    }
                    if (is CeylonPsi.SpecifierStatementPsi child) {
                        meths.add(NavigationPsiMethod(child));
                    }
                    if (is CeylonPsi.AnyAttributePsi child) {
                        meths.add(NavigationPsiMethod.getterOrSetter(child, true));
                        meths.add(NavigationPsiMethod.getterOrSetter(child, false));
                    }
                }
            }
        }

        assert(exists m = _methods);
        return m;
    }

    shared actual String name {
        if (is CeylonPsi.DeclarationPsi decl) {
            if (exists identifier = decl.ceylonNode.identifier) {
                value suffix = if (decl is CeylonPsi.ObjectDefinitionPsi|CeylonPsi.AnyMethodPsi|CeylonPsi.AnyAttributePsi)
                then "_"
                else "";

                return identifier.text + suffix;
            }
            return "<unknown>";
        } else if (is CeylonPsi.PackageDescriptorPsi decl) {
            return "$package_";
        } else if (is CeylonPsi.ModuleDescriptorPsi decl) {
            return "$module_";
        }

        Logger.getInstance(javaClass<NavigationPsiClass>())
            .error("Unhandled class of type " + className(decl));

        return "<unknown>";
    }

    qualifiedName => null;

    \iinterface => false;

    annotationType => false;

    enum => false;

    extendsList => null;

    implementsList => null;
    
    extendsListTypes => PsiClassType.emptyArray;

    implementsListTypes => PsiClassType.emptyArray;

    superClass => null;

    interfaces => PsiClass.emptyArray;

    supers => PsiClass.emptyArray;

    superTypes => PsiClassType.emptyArray;

    fields => PsiField.emptyArray;

    methods => PsiMethod.emptyArray;

    constructors => PsiMethod.emptyArray;

    initializers => PsiClassInitializer.emptyArray;

    allFields => PsiField.emptyArray;

    allMethods => PsiMethod.emptyArray;

    allInnerClasses => PsiClass.emptyArray;

    findFieldByName(String name, Boolean checkBases) => null;

    findMethodBySignature(PsiMethod patternMethod, Boolean checkBases) => null;

    findMethodsBySignature(PsiMethod patternMethod, Boolean checkBases) => PsiMethod.emptyArray;

    findMethodsAndTheirSubstitutorsByName(String name, Boolean checkBases)
            => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();

    allMethodsAndTheirSubstitutors => Collections.emptyList<Pair<PsiMethod,PsiSubstitutor>>();

    findInnerClassByName(String name, Boolean checkBases) => null;

    lBrace => null;

    rBrace => null;

    nameIdentifier => null;

    scope => null;

    isInheritor(PsiClass baseClass, Boolean checkDeep) => false;

    isInheritorDeep(PsiClass baseClass, PsiClass classToByPass) => false;

    containingClass => null;

    visibleSignatures => Collections.emptyList<HierarchicalMethodSignature>();

    setName(String name) => null;

    docComment => null;

    deprecated => false;

    hasTypeParameters() => false;

    typeParameterList => null;

    typeParameters => PsiTypeParameter.emptyArray;

    presentation => null;

    shared actual void navigate(Boolean requestFocus) {
        decl.navigate(requestFocus);
    }

    canNavigate() => true;

    canNavigateToSource() => true;

    modifierList => null;

    hasModifierProperty(String name) => false;

    project => decl.project;

    language => CeylonLanguage.instance;

    manager => null;

    children => PsiElement.emptyArray;

    parent => null;

    firstChild => null;

    lastChild => null;

    nextSibling => null;

    prevSibling => null;

    containingFile => decl.containingFile;

    textRange => null;

    startOffsetInParent => 0;

    textLength => 0;

    findElementAt(Integer offset) => null;

    findReferenceAt(Integer offset) => null;

    textOffset => 0;

    text => null;

    textToCharArray() => CharArray(0);

    shared actual PsiElement navigationElement {
        return decl;
    }

    originalElement => null;

    shared actual Boolean textMatches(CharSequence text) {
        return false;
    }

    shared actual Boolean textMatches(PsiElement element) {
        return false;
    }

    textContains(Character c) => false;

    shared actual void accept(PsiElementVisitor visitor) {}

    shared actual void acceptChildren(PsiElementVisitor visitor) {}

    copy() => null;

    add(PsiElement element) => null;

    addBefore(PsiElement element, PsiElement anchor) => null;

    addAfter(PsiElement element, PsiElement anchor) => null;

    shared actual void checkAdd(PsiElement element) {}

    addRange(PsiElement first, PsiElement last) => null;

    addRangeBefore(PsiElement first, PsiElement last, PsiElement anchor) => null;

    addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) => null;

    shared actual void delete() {}

    shared actual void checkDelete() {}

    shared actual void deleteChildRange(PsiElement first, PsiElement last) {}

    replace(PsiElement newElement) => null;

    shared actual Boolean valid {
        return true;
    }

    shared actual Boolean writable {
        return false;
    }

    reference => null;

    references => PsiReference.emptyArray;

    shared actual T? getCopyableUserData<T>(Key<T> key) given T satisfies Object {
        return null;
    }

    shared actual void putCopyableUserData<T>(Key<T> key, T \ivalue) given T satisfies Object {}

    processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) => false;

    context => null;

    physical => false;

    resolveScope => decl.resolveScope;

    useScope => decl.useScope;

    node => null;

    isEquivalentTo(PsiElement another) => false;

    getIcon(Integer flags) => null;

    shared actual T? getUserData<T>(Key<T> key) given T satisfies Object {
        return null;
    }

    shared actual void putUserData<T>(Key<T> key, T \ivalue) given T satisfies Object {}

    string => "NavigationPsiClass:" + name;
}
