//package org.intellij.plugins.ceylon.ide.lightpsi;
//
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.Pair;
//import com.intellij.psi.*;
//import com.intellij.psi.impl.PsiClassImplUtil;
//import com.intellij.psi.impl.light.LightElement;
//import com.intellij.psi.impl.light.LightModifierList;
//import com.intellij.psi.impl.source.ClassInnerStuffCache;
//import com.intellij.psi.impl.source.PsiExtensibleClass;
//import com.intellij.psi.javadoc.PsiDocComment;
//import com.intellij.psi.scope.PsiScopeProcessor;
//import com.intellij.psi.util.PsiUtil;
//import com.intellij.util.IncorrectOperationException;
//import com.redhat.ceylon.ide.common.model.asjava.AbstractClassMirror;
//import com.redhat.ceylon.ide.common.model.asjava.ceylonToJavaMapper_;
//import com.redhat.ceylon.model.loader.mirror.ClassMirror;
//import com.redhat.ceylon.model.loader.mirror.MethodMirror;
//import com.redhat.ceylon.model.loader.mirror.TypeMirror;
//import com.redhat.ceylon.model.loader.mirror.TypeParameterMirror;
//import com.redhat.ceylon.model.typechecker.model.Declaration;
//import org.intellij.plugins.ceylon.ide.lang.CeylonLanguage;
//import org.intellij.plugins.ceylon.ide.psi.CeylonTreeUtil;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//
//public class CeyLightClass extends LightElement implements PsiExtensibleClass, CeylonLightElement {
//
//    private AbstractClassMirror delegate;
//
//    private final ClassInnerStuffCache myInnersCache = new ClassInnerStuffCache(this);
//
//    public CeyLightClass(Declaration delegate, Project project) {
//        super(PsiManager.getInstance(project), CeylonLanguage.INSTANCE);
//        this.delegate = (AbstractClassMirror) ceylonToJavaMapper_.get_().mapDeclaration(delegate).getFirst();
//    }
//
//    public CeyLightClass(AbstractClassMirror mirror, Project project) {
//        super(PsiManager.getInstance(project), CeylonLanguage.INSTANCE);
//        delegate = mirror;
//    }
//
//    @Override
//    public Declaration getDeclaration() {
//        return delegate.getDecl();
//    }
//
//    @Override
//    public PsiModifierList getModifierList() {
//        return new LightModifierList(getManager(), getLanguage(), "public");
//    }
//
//    @Override
//    public boolean hasModifierProperty(String name) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public PsiDocComment getDocComment() {
//        return null;
//    }
//
//    @Override
//    public boolean isDeprecated() {
//        return delegate.getDecl().isDeprecated();
//    }
//
//    @Override
//    public boolean hasTypeParameters() {
//        return !delegate.getTypeParameters().isEmpty();
//    }
//
//    @Override
//    public PsiTypeParameterList getTypeParameterList() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @NotNull
//    @Override
//    public PsiTypeParameter[] getTypeParameters() {
//        List<TypeParameterMirror> list = delegate.getTypeParameters();
//        PsiTypeParameter[] params = new PsiTypeParameter[list.size()];
//        int i = 0;
//
//        for (TypeParameterMirror mirror : list) {
//            params[i++] = new CeyLightTypeParameter(mirror, getManager());
//        }
//        return params;
//    }
//
//    @Override
//    public String getQualifiedName() {
//        return delegate.getQualifiedName();
//    }
//
//    @Override
//    public boolean isInterface() {
//        return delegate.isInterface();
//    }
//
//    @Override
//    public boolean isAnnotationType() {
//        return delegate.isAnnotationType();
//    }
//
//    @Override
//    public boolean isEnum() {
//        return delegate.isEnum();
//    }
//
//    @Override
//    public PsiReferenceList getExtendsList() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiReferenceList getImplementsList() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiClassType[] getExtendsListTypes() {
//        // TODO Auto-generated method stub
//        return PsiClassType.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiClassType[] getImplementsListTypes() {
//        // TODO Auto-generated method stub
//        return PsiClassType.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiClass getSuperClass() {
//        if (delegate.getSuperclass() != null) {
//            System.out.println(delegate.getSuperclass());
//            //return new CeyLightClass(delegate.getSuperclass());
//        }
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiClass[] getInterfaces() {
//        // TODO Auto-generated method stub
//        return PsiClass.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiClass[] getSupers() {
//        // TODO Auto-generated method stub
//        return PsiClass.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiClassType[] getSuperTypes() {
//        List<PsiClassType> superTypes = new ArrayList<>();
//
//        if (delegate.getSuperclass() != null) {
//            superTypes.add(new CeyLightType(delegate.getSuperclass(), getProject()));
//        }
//        for (TypeMirror intf : delegate.getInterfaces()) {
//            superTypes.add(new CeyLightType(intf, getProject()));
//        }
//
//        if (superTypes.size() == 0) {
//            return PsiClassType.EMPTY_ARRAY;
//        } else {
//            return superTypes.toArray(PsiClassType.ARRAY_FACTORY.create(superTypes.size()));
//        }
//    }
//
//    @Override
//    public PsiField[] getFields() {
//        return myInnersCache.getFields();
//    }
//
//    @Override
//    public PsiMethod[] getMethods() {
//        return myInnersCache.getMethods();
//    }
//
//    @Override
//    public PsiMethod[] getConstructors() {
//        return myInnersCache.getConstructors();
//    }
//
//    @Override
//    public PsiClass[] getInnerClasses() {
//        return myInnersCache.getInnerClasses();
//    }
//
//    @Override
//    public PsiClassInitializer[] getInitializers() {
//        // TODO Auto-generated method stub
//        return PsiClassInitializer.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiField[] getAllFields() {
//        return PsiClassImplUtil.getAllFields(this);
//    }
//
//    @Override
//    public PsiMethod[] getAllMethods() {
//        return PsiClassImplUtil.getAllMethods(this);
//    }
//
//    @Override
//    public PsiClass[] getAllInnerClasses() {
//        // TODO Auto-generated method stub
//        return PsiClass.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiField findFieldByName(String name, boolean checkBases) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiMethod findMethodBySignature(PsiMethod patternMethod,
//                                           boolean checkBases) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiMethod[] findMethodsBySignature(PsiMethod patternMethod,
//                                              boolean checkBases) {
//        // TODO Auto-generated method stub
//        return PsiMethod.EMPTY_ARRAY;
//    }
//
//    @Override
//    public PsiMethod[] findMethodsByName(String name, boolean checkBases) {
//        // TODO Auto-generated method stub
//        return PsiMethod.EMPTY_ARRAY;
//    }
//
//    @Override
//    public List<Pair<PsiMethod, PsiSubstitutor>> findMethodsAndTheirSubstitutorsByName(
//            String name, boolean checkBases) {
//        // TODO Auto-generated method stub
//        return Collections.emptyList();
//    }
//
//    @Override
//    public List<Pair<PsiMethod, PsiSubstitutor>> getAllMethodsAndTheirSubstitutors() {
//        // TODO Auto-generated method stub
//        return Collections.emptyList();
//    }
//
//    @Override
//    public PsiClass findInnerClassByName(String name, boolean checkBases) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiElement getLBrace() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiElement getRBrace() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiIdentifier getNameIdentifier() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public PsiElement getScope() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public boolean isInheritor(PsiClass baseClass, boolean checkDeep) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public boolean isInheritorDeep(PsiClass baseClass, PsiClass classToByPass) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//    @Override
//    public PsiClass getContainingClass() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public Collection<HierarchicalMethodSignature> getVisibleSignatures() {
//        // TODO Auto-generated method stub
//        return Collections.emptyList();
//    }
//
//    @Override
//    public PsiElement setName(String name) throws IncorrectOperationException {
//        throw new IncorrectOperationException("Not supported");
//    }
//
//    @Override
//    public PsiFile getContainingFile() {
//        return CeylonTreeUtil.getDeclaringFile(delegate.getDecl().getUnit(), getProject());
//    }
//
//    @Override
//    public String getName() {
//        return delegate.getName();
//    }
//
//    @Override
//    public String toString() {
//        return "CeyLightClass:" + delegate.getName();
//    }
//
//    @NotNull
//    @Override
//    public List<PsiField> getOwnFields() {
//        return Collections.emptyList();
//    }
//
//    @NotNull
//    @Override
//    public List<PsiMethod> getOwnMethods() {
//        List<PsiMethod> methods = new ArrayList<>();
//
//        for (MethodMirror meth : delegate.getDirectMethods()) {
//            methods.add(new CeyLightMethod(this, meth, getProject()));
//        }
//
//        return methods;
//    }
//
//    @Override
//    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
//                                       @NotNull ResolveState state, PsiElement lastParent,
//                                       @NotNull PsiElement place) {
//        return PsiClassImplUtil.processDeclarationsInClass(this, processor, state, null,
//                lastParent, place, PsiUtil.getLanguageLevel(place), false);
//    }
//
//    @NotNull
//    @Override
//    public List<PsiClass> getOwnInnerClasses() {
//        List<PsiClass> classes = new ArrayList<>();
//
//        for (ClassMirror cls : delegate.getDirectInnerClasses()) {
//            classes.add(new CeyLightClass((AbstractClassMirror) cls, getProject()));
//        }
//
//        return classes;
//    }
//}
