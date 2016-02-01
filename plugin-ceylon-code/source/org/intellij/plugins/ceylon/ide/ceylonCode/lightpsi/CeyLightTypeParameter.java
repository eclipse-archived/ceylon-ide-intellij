package org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightEmptyImplementsList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.model.loader.mirror.TypeParameterMirror;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CeyLightTypeParameter implements PsiTypeParameter {

    private TypeParameterMirror delegate;
    private PsiManager manager;

    CeyLightTypeParameter(TypeParameterMirror delegate, PsiManager manager) {
        this.delegate = delegate;
        this.manager = manager;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return delegate.getName();
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAnnotationType() {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @NotNull
    @Override
    public PsiReferenceList getExtendsList() {
        return new LightEmptyImplementsList(manager);
    }

    @Nullable
    @Override
    public PsiReferenceList getImplementsList() {
        return null;
    }

    @NotNull
    @Override
    public PsiClassType[] getExtendsListTypes() {
        return PsiClassType.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClassType[] getImplementsListTypes() {
        return PsiClassType.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public PsiClass getSuperClass() {
        return null;
    }

    @Override
    public PsiClass[] getInterfaces() {
        return PsiClass.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClass[] getSupers() {
        return PsiClass.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClassType[] getSuperTypes() {
        return PsiClassType.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiField[] getFields() {
        return PsiField.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiMethod[] getMethods() {
        return PsiMethod.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiMethod[] getConstructors() {
        return PsiMethod.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClass[] getInnerClasses() {
        return PsiClass.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClassInitializer[] getInitializers() {
        return PsiClassInitializer.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiField[] getAllFields() {
        return PsiField.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiMethod[] getAllMethods() {
        return PsiMethod.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiClass[] getAllInnerClasses() {
        return PsiClass.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public PsiField findFieldByName(@NonNls String name, boolean checkBases) {
        return null;
    }

    @Nullable
    @Override
    public PsiMethod findMethodBySignature(PsiMethod patternMethod, boolean checkBases) {
        return null;
    }

    @NotNull
    @Override
    public PsiMethod[] findMethodsBySignature(PsiMethod patternMethod, boolean checkBases) {
        return PsiMethod.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiMethod[] findMethodsByName(@NonNls String name, boolean checkBases) {
        return PsiMethod.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(@NonNls String name, boolean checkBases) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public PsiClass findInnerClassByName(@NonNls String name, boolean checkBases) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getLBrace() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getRBrace() {
        return null;
    }

    @Nullable
    @Override
    public PsiIdentifier getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement getScope() {
        return null;
    }

    @Override
    public boolean isInheritor(@NotNull PsiClass baseClass, boolean checkDeep) {
        return false;
    }

    @Override
    public boolean isInheritorDeep(PsiClass baseClass, @Nullable PsiClass classToByPass) {
        return false;
    }

    @Nullable
    @Override
    public PsiClass getContainingClass() {
        return null;
    }

    @NotNull
    @Override
    public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) {
        return null;
    }

    @Nullable
    @Override
    public PsiTypeParameterListOwner getOwner() {
        return null;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @NotNull
    @Override
    public PsiAnnotation[] getAnnotations() {
        return PsiAnnotation.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiAnnotation[] getApplicableAnnotations() {
        return PsiAnnotation.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public PsiAnnotation findAnnotation(@NotNull @NonNls String qualifiedName) {
        return null;
    }

    @NotNull
    @Override
    public PsiAnnotation addAnnotation(@NotNull @NonNls String qualifiedName) {
        throw new UnsupportedOperationException();
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
        return PsiTypeParameter.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
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

    @Nullable
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
    public Project getProject() throws PsiInvalidElementAccessException {
        return manager.getProject();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return CeylonLanguage.INSTANCE;
    }

    @Override
    public PsiManager getManager() {
        return manager;
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return PsiElement.EMPTY_ARRAY;
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

    @Override
    public PsiElement getNavigationElement() {
        return null;
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
        return PsiReference.EMPTY_ARRAY;
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
        return null;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return null;
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
