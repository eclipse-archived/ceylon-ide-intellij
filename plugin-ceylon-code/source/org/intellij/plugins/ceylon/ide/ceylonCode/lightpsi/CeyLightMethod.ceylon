import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Key,
    UserDataHolderBase
}
import com.intellij.psi {
    PsiClass,
    PsiManager,
    PsiMethod,
    PsiType,
    PsiTypeElement,
    PsiParameterList,
    PsiReferenceList,
    PsiCodeBlock,
    PsiSubstitutor,
    PsiIdentifier,
    PsiModifierList,
    PsiModifier,
    PsiElement,
    HierarchicalMethodSignature,
    PsiTypeParameterList,
    PsiTypeParameter,
    PsiFile
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
import com.intellij.psi.javadoc {
    PsiDocComment
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.psi.util {
    MethodSignature,
    MethodSignatureBackedByPsiMethod
}
import com.intellij.util {
    IncorrectOperationException
}
import com.redhat.ceylon.model.loader.mirror {
    MethodMirror,
    TypeMirror,
    TypeKind
}

import java.lang {
    ObjectArray
}
import java.util {
    Collections,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import com.redhat.ceylon.ide.common.model.asjava {
    AbstractMethodMirror,
    GetMethod
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}
import com.intellij.pom {
    Navigatable
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    IdeaNavigation
}

shared class CeyLightMethod(shared actual PsiClass containingClass, MethodMirror mirror, Project project)
        extends LightElement(PsiManager.getInstance(project), CeylonLanguage.instance)
        satisfies PsiMethod & CeylonLightElement {
    
    shared actual Declaration declaration {
        if (is AbstractMethodMirror mirror) {
            return mirror.decl;
        }
        assert(is GetMethod mirror);
        return mirror.declaration;
    }

    shared actual PsiType? returnType {
        if (mirror.declaredVoid) {
            return PsiType.\ivoid;
        }
        return toPsiType(mirror.returnType);
    }

    shared actual PsiTypeElement? returnTypeElement {
        return null;
    }

    shared actual PsiParameterList parameterList {
        value builder = LightParameterListBuilder(manager, language);
        for (p in mirror.parameters) {
            builder.addParameter(LightParameter(p.name, toPsiType(p.type), this, CeylonLanguage.instance));
        }
        return builder;
    }

    shared actual PsiReferenceList throwsList {
        return LightEmptyImplementsList(manager);
    }

    shared actual PsiCodeBlock? body {
        return null;
    }

    constructor => mirror.constructor;

    varArgs => mirror.variadic;

    shared actual MethodSignature getSignature(PsiSubstitutor substitutor) {
        return MethodSignatureBackedByPsiMethod.create(this, substitutor);
    }

    shared actual PsiIdentifier? nameIdentifier {
        return null;
    }

    shared actual ObjectArray<PsiMethod> findSuperMethods() {
        return PsiMethod.emptyArray;
    }

    shared actual ObjectArray<PsiMethod> findSuperMethods(Boolean checkAccess) {
        return PsiMethod.emptyArray;
    }

    shared actual ObjectArray<PsiMethod> findSuperMethods(PsiClass parentClass) {
        return PsiMethod.emptyArray;
    }

    shared actual List<MethodSignatureBackedByPsiMethod> findSuperMethodSignaturesIncludingStatic(Boolean checkAccess) {
        return Collections.emptyList<MethodSignatureBackedByPsiMethod>();
    }

    shared actual PsiMethod? findDeepestSuperMethod() {
        return null;
    }

    shared actual ObjectArray<PsiMethod> findDeepestSuperMethods() {
        return PsiMethod.emptyArray;
    }

    shared actual PsiModifierList modifierList {
        return LightModifierList(manager, language, "public");
    }

    shared actual Boolean hasModifierProperty(String name) {
        if (name.equals(PsiModifier.static)) {
            return mirror.static;
        }
        return false;
    }

    shared actual PsiElement setName(String name) {
        throw IncorrectOperationException("Not supported");
    }

    shared actual HierarchicalMethodSignature hierarchicalMethodSignature {
        return PsiSuperMethodImplUtil.getHierarchicalMethodSignature(this);
    }

    shared actual PsiDocComment? docComment {
        return null;
    }

    shared actual Boolean deprecated {
        return false;
    }

    shared actual Boolean hasTypeParameters() {
        return false;
    }

    shared actual PsiTypeParameterList? typeParameterList {
        return null;
    }

    shared actual ObjectArray<PsiTypeParameter> typeParameters {
        return ObjectArray<PsiTypeParameter>(0);
    }

    shared actual String name {
        return mirror.name;
    }

    shared actual PsiFile containingFile {
        return containingClass.containingFile;
    }

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
        } else if (type.qualifiedName.equals("ceylon.language::Object")) {
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
            given T satisfies Object {
        (super of UserDataHolderBase).putCopyableUserData(key, val);
    }

    shared actual PsiElement? navigationElement => (super of LightElement).navigationElement;
    assign navigationElement {
        (super of LightElement).navigationElement = navigationElement;
    }
}
