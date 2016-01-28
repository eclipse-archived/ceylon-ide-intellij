package org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi;

import com.intellij.openapi.project.Project;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.impl.EmptySubstitutorImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.ide.common.model.asjava.AbstractClassMirror;
import com.redhat.ceylon.ide.common.model.asjava.JClassMirror;
import com.redhat.ceylon.model.loader.mirror.ClassMirror;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CeyLightType extends PsiClassType {

    private TypeMirror delegate;
    private Project project;

    private LanguageLevel languageLevel = LanguageLevel.HIGHEST;

    private ClassResolveResult classResolveResult = new ClassResolveResult() {
        @Override
        public PsiClass getElement() {
            ClassMirror mirror = delegate.getDeclaredClass();
            return mirror instanceof AbstractClassMirror
                    ? new CeyLightClass((AbstractClassMirror) mirror, project)
                    : null;
        }

        @NotNull
        @Override
        public PsiSubstitutor getSubstitutor() {
            return new EmptySubstitutorImpl();
        }

        @Override
        public boolean isPackagePrefixPackageReference() {
            return false;
        }

        @Override
        public boolean isAccessible() {
            return true;
        }

        @Override
        public boolean isStaticsScopeCorrect() {
            return true;
        }

        @Override
        public PsiElement getCurrentFileResolveScope() {
            return null;
        }

        @Override
        public boolean isValidResult() {
            return true;
        }
    };

    CeyLightType(TypeMirror delegate, Project project) {
        super(LanguageLevel.HIGHEST, PsiAnnotation.EMPTY_ARRAY);
        this.delegate = delegate;
        this.project = project;
    }

    @NotNull
    @Override
    public String getPresentableText() {
        return delegate.toString();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return delegate.getQualifiedName().replace("::", ".");
    }

    @NotNull
    @Override
    public String getInternalCanonicalText() {
        return delegate.getQualifiedName();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equalsToText(@NotNull @NonNls String text) {
        return false;
    }

    @Override
    public <A> A accept(@NotNull PsiTypeVisitor<A> visitor) {
        return visitor.visitClassType(this);
    }

    @NotNull
    @Override
    public LanguageLevel getLanguageLevel() {
        return languageLevel;
    }

    @NotNull
    @Override
    public PsiClassType setLanguageLevel(@NotNull LanguageLevel languageLevel) {
        this.languageLevel = languageLevel;
        return this;
    }

    @Override
    @NotNull
    public GlobalSearchScope getResolveScope() {
        return GlobalSearchScope.EMPTY_SCOPE;
    }

    @Nullable
    @Override
    public PsiClass resolve() {
        return null;
    }

    @Override
    public String getClassName() {
        ClassMirror cls = delegate.getDeclaredClass();
        return cls == null ? "unknown" : cls.getName();
    }

    @NotNull
    @Override
    public PsiType[] getParameters() {
        return new PsiType[0];
    }

    @NotNull
    @Override
    public PsiType[] getSuperTypes() {
        return new PsiType[0];
    }

    @NotNull
    @Override
    public ClassResolveResult resolveGenerics() {
        return classResolveResult;
    }

    @NotNull
    @Override
    public PsiClassType rawType() {
        return this;
    }
}
