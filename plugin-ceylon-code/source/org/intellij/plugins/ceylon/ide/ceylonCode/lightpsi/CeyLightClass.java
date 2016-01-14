package org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiTypeParameterList;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.impl.light.LightModifierList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Interface;

public class CeyLightClass extends LightElement implements PsiClass {

    private ClassOrInterface delegate;

    public CeyLightClass(ClassOrInterface delegate, Project project) {
        super(PsiManager.getInstance(project), CeylonLanguage.INSTANCE);
        this.delegate = delegate;
    }

    public ClassOrInterface getDelegate() {
        return delegate;
    }

    @Override
    public PsiModifierList getModifierList() {
        return new LightModifierList(getManager(), getLanguage(), "public");
    }

    @Override
    public boolean hasModifierProperty(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public PsiDocComment getDocComment() {
        return null;
    }

    @Override
    public boolean isDeprecated() {
        return delegate.isDeprecated();
    }

    @Override
    public boolean hasTypeParameters() {
        return !delegate.getTypeParameters().isEmpty();
    }

    @Override
    public PsiTypeParameterList getTypeParameterList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiTypeParameter[] getTypeParameters() {
        return PsiTypeParameter.EMPTY_ARRAY;
    }

    @Override
    public String getQualifiedName() {
        return delegate.getQualifiedNameString().replace("::", ".");
    }

    @Override
    public boolean isInterface() {
        return delegate instanceof Interface;
    }

    @Override
    public boolean isAnnotationType() {
        return delegate.isAnnotation();
    }

    @Override
    public boolean isEnum() {
        return delegate.isJavaEnum();
    }

    @Override
    public PsiReferenceList getExtendsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiReferenceList getImplementsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiClassType[] getExtendsListTypes() {
        // TODO Auto-generated method stub
        return PsiClassType.EMPTY_ARRAY;
    }

    @Override
    public PsiClassType[] getImplementsListTypes() {
        // TODO Auto-generated method stub
        return PsiClassType.EMPTY_ARRAY;
    }

    @Override
    public PsiClass getSuperClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiClass[] getInterfaces() {
        // TODO Auto-generated method stub
        return PsiClass.EMPTY_ARRAY;
    }

    @Override
    public PsiClass[] getSupers() {
        // TODO Auto-generated method stub
        return PsiClass.EMPTY_ARRAY;
    }

    @Override
    public PsiClassType[] getSuperTypes() {
        // TODO Auto-generated method stub
        return PsiClassType.EMPTY_ARRAY;
    }

    @Override
    public PsiField[] getFields() {
        // TODO Auto-generated method stub
        return PsiField.EMPTY_ARRAY;
    }

    @Override
    public PsiMethod[] getMethods() {
        // TODO Auto-generated method stub
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public PsiMethod[] getConstructors() {
        // TODO Auto-generated method stub
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public PsiClass[] getInnerClasses() {
        // TODO Auto-generated method stub
        return PsiClass.EMPTY_ARRAY;
    }

    @Override
    public PsiClassInitializer[] getInitializers() {
        // TODO Auto-generated method stub
        return PsiClassInitializer.EMPTY_ARRAY;
    }

    @Override
    public PsiField[] getAllFields() {
        // TODO Auto-generated method stub
        return PsiField.EMPTY_ARRAY;
    }

    @Override
    public PsiMethod[] getAllMethods() {
        // TODO Auto-generated method stub
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public PsiClass[] getAllInnerClasses() {
        // TODO Auto-generated method stub
        return PsiClass.EMPTY_ARRAY;
    }

    @Override
    public PsiField findFieldByName(String name, boolean checkBases) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiMethod findMethodBySignature(PsiMethod patternMethod,
            boolean checkBases) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiMethod[] findMethodsBySignature(PsiMethod patternMethod,
            boolean checkBases) {
        // TODO Auto-generated method stub
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public PsiMethod[] findMethodsByName(String name, boolean checkBases) {
        // TODO Auto-generated method stub
        return PsiMethod.EMPTY_ARRAY;
    }

    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(
            String name, boolean checkBases) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public PsiClass findInnerClassByName(String name, boolean checkBases) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiElement getLBrace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiElement getRBrace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiIdentifier getNameIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PsiElement getScope() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInheritor(PsiClass baseClass, boolean checkDeep) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInheritorDeep(PsiClass baseClass, PsiClass classToByPass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public PsiClass getContainingClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public PsiElement setName(String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not supported");
    }

    @Override
    public PsiFile getContainingFile() {
        return CeylonTreeUtil.getDeclaringFile(delegate.getUnit(), getProject());
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String toString() {
        return "Ceylon light class " + delegate.getName();
    }
}
