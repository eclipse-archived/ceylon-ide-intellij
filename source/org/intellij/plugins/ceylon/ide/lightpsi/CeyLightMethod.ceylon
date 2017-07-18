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
    AbstractMethodMirror
}
import com.redhat.ceylon.model.loader.mirror {
    TypeMirror,
    TypeKind
}

import java.lang {
    ObjectArray,
    overloaded
}
import java.util {
    Collections
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.resolve {
    IdeaNavigation
}

shared class CeyLightMethod(containingClass, mirror, project)
        extends LightElement(PsiManager.getInstance(project), ceylonLanguage)
        satisfies PsiMethod & CeylonLightElement {

    shared actual PsiClass containingClass;
    Project project;
    AbstractMethodMirror mirror;

    declaration => mirror.declaration;

    returnType => mirror.declaredVoid then PsiType.\ivoid else toPsiType(mirror.returnType);

    returnTypeElement => null;

    shared actual PsiParameterList parameterList {
        value builder = LightParameterListBuilder(manager, language);
        for (p in mirror.parameters) {
            if (exists type = toPsiType(p.type)) {
                builder.addParameter(LightParameter(p.name, type, this, ceylonLanguage));
            }
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

    overloaded
    shared actual ObjectArray<PsiMethod> findSuperMethods() => PsiMethod.emptyArray;

    overloaded
    shared actual ObjectArray<PsiMethod> findSuperMethods(Boolean checkAccess) => PsiMethod.emptyArray;

    overloaded
    shared actual ObjectArray<PsiMethod> findSuperMethods(PsiClass parentClass) => PsiMethod.emptyArray;

    findSuperMethodSignaturesIncludingStatic(Boolean checkAccess)
            => Collections.emptyList<MethodSignatureBackedByPsiMethod>();

    findDeepestSuperMethod() => null;

    findDeepestSuperMethods() => PsiMethod.emptyArray;

    modifierList => LightModifierList(manager, language, "public");

    hasModifierProperty(String name)
            => name==PsiModifier.static then mirror.static else false;

    shared actual PsiElement setName(String name) {
        throw IncorrectOperationException("Not supported");
    }

    hierarchicalMethodSignature => PsiSuperMethodImplUtil.getHierarchicalMethodSignature(this);

    docComment => null;

    deprecated => mirror.decl.deprecated;

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
        } else if (type.qualifiedName=="java.lang.String") {
            return PsiType.getJavaLangString(manager, GlobalSearchScope.allScope(project));
        } else if (type.qualifiedName=="ceylon.language::Object"
                || type.qualifiedName=="java.lang.Object") {
            return PsiType.getJavaLangObject(manager, GlobalSearchScope.allScope(project));
        }
        return CeyLightType(type, project);
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
