import ceylon.interop.java {
    CeylonIterable
}

import com.intellij.openapi.util {
    Key
}
import com.intellij.psi {
    ...
}
import com.intellij.psi.impl.light {
    LightParameter,
    LightParameterListBuilder,
    LightTypeParameterBuilder
}
import com.intellij.psi.impl.source {
    PsiImmediateClassType
}
import com.intellij.psi.scope {
    PsiScopeProcessor
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.psi.util {
    MethodSignature,
    MethodSignatureBackedByPsiMethod,
    MethodSignatureBase
}
import com.redhat.ceylon.compiler.java.util {
    Util
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import java.lang {
    ObjectArray,
    CharArray,
    CharSequence
}
import java.util {
    List,
    Collections
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}

"Wraps a Function in a PsiMethod. This is used to navigate from compiled classes to the
 corresponding Ceylon code, for example in Go To Implementations or Navigate > Symbol."
class NavigationPsiMethod satisfies PsiMethod {

    variable Boolean isGetter;
    Boolean isSetter;
    CeylonCompositeElement func;
    Integer paramsCount;

    shared new (CeylonCompositeElement func) {
        this.func = func;
        this.isGetter = false;
        this.isSetter = false;
        if (is CeylonPsi.SpecifierStatementPsi func) {
            CeylonPsi.SpecifierStatementPsi ss = func;
            Tree.Term bme = ss.ceylonNode.baseMemberExpression;
            if (!(bme is Tree.ParameterizedExpression)) {
                isGetter = true;
            }
        }
        this.paramsCount = -1;
    }

    shared new getterOrSetter(CeylonCompositeElement func, Boolean isGetter) {
        this.func = func;
        this.isGetter = isGetter;
        this.isSetter = !isGetter;
        this.paramsCount = -1;
    }

    shared new forDefaultArgs(CeylonCompositeElement func, Integer paramsCount) {
        this.func = func;
        this.isGetter = false;
        this.isSetter = false;
        this.paramsCount = paramsCount;
    }

    value typeDescriptorClassName = "com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor";

    shared actual PsiParameterList parameterList {
        value builder = LightParameterListBuilder(func.manager, CeylonLanguage.instance);
        value scope = GlobalSearchScope.allScope(func.project);

        if (is CeylonPsi.AnyMethodPsi func,
            exists tpList = func.ceylonNode.typeParameterList) {

            variable Integer i = 0;

            while (i<tpList.typeParameterDeclarations.size()) {
                LightParameter lightParam = LightParameter(
                    "td" + i.string,
                    PsiType.getTypeByName(typeDescriptorClassName, func.project, scope),
                    func,
                    CeylonLanguage.instance
                );

                builder.addParameter(lightParam);
                i ++;
            }
        }

        if (exists parameterList = findParameterList()) {
            assert (is CeylonFile file = func.containingFile);

            file.ensureTypechecked();

            value signatureLength = paramsCount == -1
                then parameterList.parameters.size()
                else paramsCount;

            for (idx -> param in CeylonIterable(parameterList.parameters).indexed) {
                if (idx < signatureLength,
                    is Tree.ParameterDeclaration param) {
                    value typedDeclaration = param.typedDeclaration;
                    Type? typeModel = typedDeclaration.type.typeModel;

                    PsiType psiType;
                    if (exists typeModel, typeModel.typeParameter) {
                        value tp = LightTypeParameterBuilder(typedDeclaration.type.text, this, 0);
                        psiType = PsiImmediateClassType(tp, PsiSubstitutor.empty);
                    } else if (exists typeModel, typeModel.functional) {
                        psiType = PsiType.getTypeByName("ceylon.language.Callable", func.project, scope);
                    } else {
                        psiType = mapType(typeModel, scope);
                    }

                    value lightParam = LightParameter(
                        typedDeclaration.identifier.text,
                        psiType,
                        func,
                        CeylonLanguage.instance
                    );
                    builder.addParameter(lightParam);
                }
            }
        }
        return builder;
    }

    Tree.ParameterList? findParameterList() {
        if (is CeylonPsi.AnyMethodPsi func) {
            return func.ceylonNode.parameterLists.get(0);
        } else if (is CeylonPsi.SpecifierStatementPsi func) {
            value bme = func.ceylonNode.baseMemberExpression;
            if (is Tree.ParameterizedExpression bme) {
                return bme.parameterLists.get(0);
            }
        }
        return null;
    }

    PsiType mapType(Type? type, GlobalSearchScope scope) {
        if (!exists type) {
            return PsiType.getJavaLangObject(func.manager, scope);
        }
        if (type.union) {
            List<Type> types = type.caseTypes;
            variable Boolean hasNullType = false;
            for (Type t in types) {
                if (t.null) {
                    hasNullType = true;
                    break ;
                }
            }
            if (hasNullType, type.caseTypes.size() == 2) {
                variable Type? nonNullType = null;
                for (Type t in types) {
                    if (!t.null) {
                        nonNullType = t;
                    }
                }
                assert(exists nnt = nonNullType);
                return mapType(nnt, scope);
            } else {
                return PsiType.getJavaLangObject(func.manager, scope);
            }
        }
        if (type.integer) {
            return PsiType.int;
        } else if (type.float) {
            return PsiType.double;
        } else if (type.boolean) {
            return PsiType.boolean;
        } else if (type.character) {
            return PsiType.char;
        } else if (type.byte) {
            return PsiType.byte;
        } else if (type.isString()) {
            return PsiType.getJavaLangString(func.manager, scope);
        }
        String name = type.declaration.qualifiedNameString.replace("::", ".");
        return PsiType.getTypeByName(name, func.project, scope);
    }

    shared actual MethodSignature getSignature(PsiSubstitutor substitutor_) {
        value params = parameterList.parameters;
        value types = ObjectArray<PsiType>(params.size);
        variable Integer i = 0;

        for (param in params) {
            types.set(i++, param.type);
        }

        return object extends MethodSignatureBase(substitutor_, types, PsiTypeParameter.emptyArray) {
            name => outer.name;
            raw => false;
            constructor => false;
        };
    }

    shared actual String name {
        value _name = func.name else "<unknown>";

        if (_name == "string") {
            return "toString";
        } else if (_name == "hash") {
            return "hashCode";
        } else if (isSetter) {
            return "set" +Util.capitalize(_name);
        } else if (isGetter) {
            return "get" +Util.capitalize(_name);
        }
        return _name;
    }

    returnType => null;

    returnTypeElement => null;

    throwsList => nothing;

    body => null;

    constructor => false;

    varArgs => false;

    nameIdentifier => null;

    shared actual ObjectArray<PsiMethod> findSuperMethods()
            => PsiMethod.emptyArray;

    shared actual ObjectArray<PsiMethod> findSuperMethods(Boolean checkAccess)
            => PsiMethod.emptyArray;

    shared actual ObjectArray<PsiMethod> findSuperMethods(PsiClass parentClass)
            => PsiMethod.emptyArray;

    findSuperMethodSignaturesIncludingStatic(Boolean checkAccess)
            => Collections.emptyList<MethodSignatureBackedByPsiMethod>();

    findDeepestSuperMethod() => null;

    findDeepestSuperMethods() => PsiMethod.emptyArray;

    modifierList => nothing;

    hasModifierProperty(String name) => false;

    presentation => null;

    setName(String name) => null;

    hierarchicalMethodSignature => nothing;

    docComment => null;

    deprecated => false;

    hasTypeParameters() => false;

    typeParameterList => null;

    typeParameters => PsiTypeParameter.emptyArray;

    containingClass => null;

    shared actual void navigate(Boolean requestFocus) {}

    canNavigate() => false;

    canNavigateToSource() => false;

    project => func.project;

    language => CeylonLanguage.instance;

    manager => func.manager;

    children => PsiElement.emptyArray;

    parent => null;

    firstChild => null;

    lastChild => null;

    nextSibling => null;

    prevSibling => null;

    containingFile => null;

    textRange => null;

    startOffsetInParent => 0;

    textLength => 0;

    findElementAt(Integer offset) => null;

    findReferenceAt(Integer offset) => null;

    textOffset => 0;

    text => null;

    textToCharArray() => CharArray(0);

    shared actual PsiElement navigationElement => func;

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

    shared actual Boolean valid => false;

    shared actual Boolean writable => false;

    reference => null;

    references => PsiReference.emptyArray;

    shared actual T? getCopyableUserData<T>(Key<T> key) given T satisfies Object => null;

    shared actual void putCopyableUserData<T>(Key<T> key, T \ivalue) given T satisfies Object {}

    processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent,
        PsiElement place) => false;

    context => null;

    physical => false;

    resolveScope => func.resolveScope;

    useScope => func.useScope;

    node => null;

    isEquivalentTo(PsiElement another) => false;

    getIcon(Integer flags) => null;

    shared actual T? getUserData<T>(Key<T> key) given T satisfies Object => null;

    shared actual void putUserData<T>(Key<T> key, T \ivalue) given T satisfies Object {}

    string => "NavigationPsiMethod:" + name;
}
