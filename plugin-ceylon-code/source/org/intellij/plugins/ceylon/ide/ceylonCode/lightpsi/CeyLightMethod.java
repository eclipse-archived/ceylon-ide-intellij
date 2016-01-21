package org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiSuperMethodImplUtil;
import com.intellij.psi.impl.light.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.redhat.ceylon.model.loader.impl.reflect.mirror.ReflectionType;
import com.redhat.ceylon.model.loader.mirror.MethodMirror;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import com.redhat.ceylon.model.loader.mirror.VariableMirror;
import com.redhat.ceylon.model.typechecker.model.Type;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.type.TypeKind;
import java.util.Collections;
import java.util.List;

public class CeyLightMethod extends LightElement implements PsiMethod {

    @NotNull
    private final PsiClass containingClass;
    @NotNull
    private final MethodMirror delegate;

    public CeyLightMethod(@NotNull PsiClass containingClass, @NotNull MethodMirror delegate,
                          @NotNull Project project) {
        super(PsiManager.getInstance(project), CeylonLanguage.INSTANCE);
        this.containingClass = containingClass;
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public PsiType getReturnType() {
        if (delegate.isDeclaredVoid()) {
            return PsiType.VOID;
        }
        return toPsiType(delegate.getReturnType());
    }

    @Nullable
    @Override
    public PsiTypeElement getReturnTypeElement() {
        return null;
    }

    @NotNull
    @Override
    public PsiParameterList getParameterList() {
        LightParameterListBuilder builder = new LightParameterListBuilder(getManager(), getLanguage());

        for (VariableMirror p : delegate.getParameters()) {
            builder.addParameter(new LightParameter(p.getName(), toPsiType(p.getType()),
                    this, CeylonLanguage.INSTANCE));
        }
        return builder;
    }

    @NotNull
    @Override
    public PsiReferenceList getThrowsList() {
        return new LightEmptyImplementsList(getManager());
    }

    @Nullable
    @Override
    public PsiCodeBlock getBody() {
        return null;
    }

    @Override
    public boolean isConstructor() {
        // todo
        return false;
    }

    @Override
    public boolean isVarArgs() {
        // todo
        return false;
    }

    @NotNull
    @Override
    public MethodSignature getSignature(@NotNull PsiSubstitutor substitutor) {
        return MethodSignatureBackedByPsiMethod.create(this, substitutor);
    }

    @Nullable
    @Override
    public PsiIdentifier getNameIdentifier() {
        return null;
    }

    @NotNull
    @Override
    public PsiMethod[] findSuperMethods() {
        return new PsiMethod[0];
    }

    @NotNull
    @Override
    public PsiMethod[] findSuperMethods(boolean checkAccess) {
        return new PsiMethod[0];
    }

    @NotNull
    @Override
    public PsiMethod[] findSuperMethods(PsiClass parentClass) {
        return new PsiMethod[0];
    }

    @NotNull
    @Override
    public List<MethodSignatureBackedByPsiMethod> findSuperMethodSignaturesIncludingStatic(boolean checkAccess) {
        // todo
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public PsiMethod findDeepestSuperMethod() {
        return null;
    }

    @NotNull
    @Override
    public PsiMethod[] findDeepestSuperMethods() {
        return new PsiMethod[0];
    }

    @NotNull
    @Override
    public PsiModifierList getModifierList() {
        return new LightModifierList(getManager(), getLanguage(), "public");
    }

    @Override
    public boolean hasModifierProperty(@PsiModifier.ModifierConstant @NonNls @NotNull String name) {
        return false;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public HierarchicalMethodSignature getHierarchicalMethodSignature() {
        return PsiSuperMethodImplUtil.getHierarchicalMethodSignature(this);
    }

    @Nullable
    @Override
    public PsiDocComment getDocComment() {
        return null;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public boolean hasTypeParameters() {
        // todo return delegate.getTypeParameters().size() > 0;
        return false;
    }

    @Nullable
    @Override
    public PsiTypeParameterList getTypeParameterList() {
        return null;
    }

    @NotNull
    @Override
    public PsiTypeParameter[] getTypeParameters() {
        return new PsiTypeParameter[0];
    }

    @NotNull
    @Override
    public PsiClass getContainingClass() {
        return containingClass;
    }

    @NotNull
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public PsiFile getContainingFile() {
        return containingClass.getContainingFile();
    }

    @Override
    public String toString() {
        return "CeyLightMethod:" + delegate.getName();
    }

    @Nullable
    private PsiType toPsiType(@Nullable TypeMirror type) {
        if (type == null) {
            return null;
        }
        if (type.getKind() == TypeKind.LONG) {
            return PsiType.LONG;
        } else if (type.getKind() == TypeKind.DOUBLE) {
            return PsiType.DOUBLE;
        } else if (type.getKind() == TypeKind.BOOLEAN) {
            return PsiType.BOOLEAN;
        } else if (type.getKind() == TypeKind.INT) {
            return PsiType.INT;
        } else if (type.getKind() == TypeKind.BYTE) {
            return PsiType.BYTE;
        } else if (type.getQualifiedName().equals("java.lang::String")) {
            return PsiType.getJavaLangString(getManager(), GlobalSearchScope.projectScope(getProject()));
        } else if (type.getQualifiedName().equals("ceylon.language::Object")) {
            return PsiType.getJavaLangObject(getManager(), GlobalSearchScope.projectScope(getProject()));
        }
        return new CeyLightType(type, getProject());
    }
}
