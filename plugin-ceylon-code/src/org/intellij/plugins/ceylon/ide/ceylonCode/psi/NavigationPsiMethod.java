package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightParameter;
import com.intellij.psi.impl.light.LightParameterListBuilder;
import com.intellij.psi.impl.light.LightTypeParameterBuilder;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.psi.util.MethodSignatureBase;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Type;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Wraps a Function in a PsiMethod. This is used to navigate from compiled classes to the
 * corresponding Ceylon code, for example in Go To Implementations or Navigate > Symbol.
 */
class NavigationPsiMethod implements PsiMethod {

    private boolean isGetter;
    private boolean isSetter;
    private CeylonCompositeElement func;

    NavigationPsiMethod(CeylonCompositeElement func) {
        this.func = func;
        this.isGetter = false;
        this.isSetter = false;

        if (func instanceof CeylonPsi.SpecifierStatementPsi) {
            CeylonPsi.SpecifierStatementPsi ss = (CeylonPsi.SpecifierStatementPsi) func;
            Tree.Term bme = ss.getCeylonNode().getBaseMemberExpression();

            if (!(bme instanceof Tree.ParameterizedExpression)) {
                isGetter = true;
            }
        }
    }

    NavigationPsiMethod(CeylonCompositeElement func, boolean isGetter) {
        this.func = func;
        this.isGetter = isGetter;
        this.isSetter = !isGetter;
    }

    @Nullable
    @Override
    public PsiType getReturnType() {
        return null;
    }

    @Nullable
    @Override
    public PsiTypeElement getReturnTypeElement() {
        return null;
    }

    @NotNull
    @Override
    public PsiParameterList getParameterList() {
        LightParameterListBuilder builder =
                new LightParameterListBuilder(func.getManager(), CeylonLanguage.INSTANCE);

        GlobalSearchScope scope = GlobalSearchScope.allScope(func.getProject());

        if (func instanceof CeylonPsi.AnyMethodPsi) {
            CeylonPsi.AnyMethodPsi method = (CeylonPsi.AnyMethodPsi) func;
            Tree.TypeParameterList tpList = method.getCeylonNode().getTypeParameterList();
            if (tpList != null) {
                for (int i = 0; i < tpList.getTypeParameterDeclarations().size(); i++) {
                    LightParameter lightParam = new LightParameter(
                            "td" + i,
                            PsiType.getTypeByName(TypeDescriptor.class.getCanonicalName(), func.getProject(), scope),
                            func,
                            CeylonLanguage.INSTANCE
                    );
                    builder.addParameter(lightParam);
                }
            }

        }

        Tree.ParameterList parameterList = findParameterList();

        if (parameterList != null) {
            ((CeylonFile) func.getContainingFile()).ensureTypechecked();

            for (Tree.Parameter param : parameterList.getParameters()) {
                if (param instanceof Tree.ParameterDeclaration) {
                    Tree.TypedDeclaration typedDeclaration = ((Tree.ParameterDeclaration) param).getTypedDeclaration();
                    Type typeModel = typedDeclaration.getType().getTypeModel();
                    PsiType psiType;

                    if (typeModel != null && typeModel.isTypeParameter()) {
                        LightTypeParameterBuilder tp = new LightTypeParameterBuilder(typedDeclaration.getType().getText(), this, 0);
                        psiType = new PsiImmediateClassType(tp, PsiSubstitutor.EMPTY);
                    } else {
                        psiType = mapType(typeModel, scope);
                    }

                    LightParameter lightParam = new LightParameter(
                            typedDeclaration.getIdentifier().getText(),
                            psiType,
                            func,
                            CeylonLanguage.INSTANCE
                    );
                    builder.addParameter(lightParam);
                }
            }
        }

        return builder;
    }

    @Nullable
    private Tree.ParameterList findParameterList() {
        if (func instanceof CeylonPsi.AnyMethodPsi) {
            return ((CeylonPsi.AnyMethodPsi) func).getCeylonNode().getParameterLists().get(0);
        } else if (func instanceof CeylonPsi.SpecifierStatementPsi) {
            CeylonPsi.SpecifierStatementPsi ss = (CeylonPsi.SpecifierStatementPsi) func;
            Tree.Term bme = ss.getCeylonNode().getBaseMemberExpression();

            if (bme instanceof Tree.ParameterizedExpression) {
                return ((Tree.ParameterizedExpression) bme).getParameterLists().get(0);
            }
        }

        return null;
    }

    // Same as ceylonToJavaMapper.mapType() but for PsiTypes
    private PsiType mapType(@Nullable Type type, GlobalSearchScope scope) {
        if (type==null) {
            return PsiType.getJavaLangObject(func.getManager(), scope);
        }

        if (type.isUnion()) {
            List<Type> types = type.getCaseTypes();
            boolean hasNullType = false;

            for (Type t : types) {
                if (t.isNull()) {
                    hasNullType = true;
                    break;
                }
            }

            if (hasNullType && type.getCaseTypes().size() == 2) {
                // Return the non-null type
                Type nonNullType = null;

                for (Type t : types) {
                    if (!t.isNull()) {
                        nonNullType = t;
                    }
                }
                return mapType(nonNullType, scope);
            } else {
                return PsiType.getJavaLangObject(func.getManager(), scope);
            }
        }

        if (type.isInteger()) {
            return PsiType.INT;
        } else if (type.isFloat()) {
            return PsiType.DOUBLE;
        } else if (type.isBoolean()) {
            return PsiType.BOOLEAN;
        } else if (type.isCharacter()) {
            return PsiType.CHAR;
        } else if (type.isByte()) {
            return PsiType.BYTE;
        } else if (type.isString()) {
            return PsiType.getJavaLangString(func.getManager(), scope);
        }

        String name = type.getDeclaration().getQualifiedNameString().replace("::", ".");
        return PsiType.getTypeByName(name, func.getProject(), scope);
    }

    @NotNull
    @Override
    public PsiReferenceList getThrowsList() {
        return null;
    }

    @Nullable
    @Override
    public PsiCodeBlock getBody() {
        return null;
    }

    @Override
    public boolean isConstructor() {
        return false;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @NotNull
    @Override
    public MethodSignature getSignature(@NotNull PsiSubstitutor substitutor) {
        PsiParameter[] params = getParameterList().getParameters();
        PsiType[] types = new PsiType[params.length];
        int i = 0;
        for (PsiParameter param : params) {
            types[i++] = param.getType();
        }

        return new MethodSignatureBase(substitutor, types, PsiTypeParameter.EMPTY_ARRAY) {
            @NotNull
            @Override
            public String getName() {
                return NavigationPsiMethod.this.getName();
            }

            @Override
            public boolean isRaw() {
                return false;
            }

            @Override
            public boolean isConstructor() {
                return false;
            }
        };
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
        return null;
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
        return null;
    }

    @Override
    public boolean hasModifierProperty(@PsiModifier.ModifierConstant @NonNls @NotNull String name) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        if (func.getName().equals("string")) {
            return "toString";
        } else if (func.getName().equals("hash")) {
            return "hashCode";
        } else if (isSetter) {
            return "set" + Util.capitalize(func.getName());
        } else if (isGetter) {
            return "get" + Util.capitalize(func.getName());
        }

        return func.getName();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public HierarchicalMethodSignature getHierarchicalMethodSignature() {
        return null;
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

    @Nullable
    @Override
    public PsiClass getContainingClass() {
        return null;
    }

    @Override
    public void navigate(boolean requestFocus) {

    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    @NotNull
    @Override
    public Project getProject() throws PsiInvalidElementAccessException {
        return func.getProject();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return CeylonLanguage.INSTANCE;
    }

    @Override
    public PsiManager getManager() {
        return func.getManager();
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    @Override
    public PsiElement getParent() {
        return null;
    }

    @Override
    public PsiElement getFirstChild() {
        return null;
    }

    @Override
    public PsiElement getLastChild() {
        return null;
    }

    @Override
    public PsiElement getNextSibling() {
        return null;
    }

    @Override
    public PsiElement getPrevSibling() {
        return null;
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return null;
    }

    @Override
    public TextRange getTextRange() {
        return null;
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public int getTextLength() {
        return 0;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        return null;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    @Override
    public String getText() {
        return null;
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return new char[0];
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return func;
    }

    @Override
    public PsiElement getOriginalElement() {
        return null;
    }

    @Override
    public boolean textMatches(@NotNull @NonNls CharSequence text) {
        return false;
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean textContains(char c) {
        return false;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {

    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor visitor) {

    }

    @Override
    public PsiElement copy() {
        return null;
    }

    @Override
    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {

    }

    @Override
    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void delete() throws IncorrectOperationException {

    }

    @Override
    public void checkDelete() throws IncorrectOperationException {

    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {

    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return new PsiReference[0];
    }

    @Nullable
    @Override
    public <T> T getCopyableUserData(Key<T> key) {
        return null;
    }

    @Override
    public <T> void putCopyableUserData(Key<T> key, @Nullable T value) {

    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, @Nullable PsiElement lastParent, @NotNull PsiElement place) {
        return false;
    }

    @Nullable
    @Override
    public PsiElement getContext() {
        return null;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return func.getResolveScope();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return func.getUseScope();
    }

    @Override
    public ASTNode getNode() {
        return null;
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return false;
    }

    @Override
    public Icon getIcon(@IconFlags int flags) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }
}
