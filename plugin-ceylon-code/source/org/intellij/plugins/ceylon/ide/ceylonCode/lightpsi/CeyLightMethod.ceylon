import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Key,
    UserDataHolderBase
}
import com.intellij.pom {
    Navigatable
}
import com.intellij.psi {
    PsiClass,
    PsiManager,
    PsiMethod,
    PsiType,
    PsiParameterList,
    PsiSubstitutor,
    PsiModifier,
    PsiElement,
    PsiTypeParameter
}
import com.intellij.psi.impl {
    PsiSuperMethodImplUtil
}
import com.intellij.psi.impl.light {
    LightElement,
    LightParameterListBuilder,
    LightParameter,
    LightEmptyImplementsList,
    LightModifierList
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.psi.util {
    MethodSignatureBackedByPsiMethod
}
import com.intellij.util {
    IncorrectOperationException
}
import com.redhat.ceylon.ide.common.model.asjava {
    AbstractMethodMirror,
    GetMethod
}
import com.redhat.ceylon.model.loader.mirror {
    MethodMirror,
    TypeMirror,
    TypeKind
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
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
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    IdeaNavigation
}

shared class CeyLightMethod(containingClass, mirror, project)
        extends LightElement(PsiManager.getInstance(project), CeylonLanguage.instance)
        satisfies PsiMethod & CeylonLightElement {

    shared actual PsiClass containingClass;
    Project project;
    MethodMirror mirror;

    shared actual Declaration declaration {
        if (is AbstractMethodMirror mirror) {
            return mirror.decl;
        }
        assert(is GetMethod mirror);
        return mirror.declaration;
    }

    returnType => mirror.declaredVoid then PsiType.\ivoid else toPsiType(mirror.returnType);

    returnTypeElement => null;

    shared actual PsiParameterList parameterList {
        value builder = LightParameterListBuilder(manager, language);
        for (p in mirror.parameters) {
            builder.addParameter(LightParameter(p.name, toPsiType(p.type), this, CeylonLanguage.instance));
        }
        return builder;
    }

    throwsList => LightEmptyImplementsList(manager);

    body => null;

    constructor => mirror.constructor;

    varArgs => mirror.variadic;

    getSignature(PsiSubstitutor substitutor)
            => MethodSignatureBackedByPsiMethod.create(this, substitutor);

    nameIdentifier => null;

    shared actual ObjectArray<PsiMethod> findSuperMethods() {
        return PsiMethod.emptyArray;
    }

    shared actual ObjectArray<PsiMethod> findSuperMethods(Boolean checkAccess) {
        return PsiMethod.emptyArray;
    }

    shared actual ObjectArray<PsiMethod> findSuperMethods(PsiClass parentClass) {
        return PsiMethod.emptyArray;
    }

    findSuperMethodSignaturesIncludingStatic(Boolean checkAccess)
            => Collections.emptyList<MethodSignatureBackedByPsiMethod>();

    findDeepestSuperMethod() => null;

    findDeepestSuperMethods() => PsiMethod.emptyArray;

    modifierList => LightModifierList(manager, language, "public");

    hasModifierProperty(String name)
            => name.equals(PsiModifier.static) then mirror.static else false;

    shared actual PsiElement setName(String name) {
        throw IncorrectOperationException("Not supported");
    }

    hierarchicalMethodSignature => PsiSuperMethodImplUtil.getHierarchicalMethodSignature(this);

    docComment => null;

    deprecated => false;

    hasTypeParameters() => false;

    typeParameterList => null;

    typeParameters => ObjectArray<PsiTypeParameter>(0);

    name => mirror.name;

    containingFile => containingClass.containingFile;

    shared actual void navigate(Boolean requestFocus) {
        if (is Navigatable nav = IdeaNavigation(project).gotoDeclaration(declaration)) {
            nav.navigate(requestFocus);
        }
    }

    string => "CeyLightMethod:" + mirror.name;

    PsiType? toPsiType(TypeMirror? type) {
        if (! exists type) {
            return null;
        }
        if (type.kind == TypeKind.long) {
            return PsiType.long;
        } else if (type.kind == TypeKind.double) {
            return PsiType.double;
        } else if (type.kind == TypeKind.boolean) {
            return PsiType.boolean;
        } else if (type.kind == TypeKind.int) {
            return PsiType.int;
        } else if (type.kind == TypeKind.byte) {
            return PsiType.byte;
        } else if (type.qualifiedName.equals("java.lang.String")) {
            return PsiType.getJavaLangString(manager, GlobalSearchScope.allScope(project));
        } else if (type.qualifiedName.equals("ceylon.language::Object")
            || type.qualifiedName.equals("java.lang.Object")) {
            return PsiType.getJavaLangObject(manager, GlobalSearchScope.allScope(project));
        }
        return CeyLightType(type, project);
    }

    // Ambiguous members inherited from multiple parents
    valid => (super of LightElement).valid;

    writable => (super of LightElement).writable;

    shared actual T getCopyableUserData<T>(Key<T>? key)
            given T satisfies Object
            => (super of UserDataHolderBase).getCopyableUserData(key);

    shared actual void putCopyableUserData<T>(Key<T>? key, T? val)
            given T satisfies Object
            => (super of UserDataHolderBase).putCopyableUserData(key, val);

    shared actual PsiElement? navigationElement
            => (super of LightElement).navigationElement;
    assign navigationElement
            => (super of LightElement).navigationElement = navigationElement;
}
