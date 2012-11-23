// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCompilationUnitImpl extends CeylonCompositeElementImpl implements CeylonCompilationUnit {

  public CeylonCompilationUnitImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonAnnotations getAnnotations() {
    return findChildByClass(CeylonAnnotations.class);
  }

  @Override
  @NotNull
  public List<CeylonCompilerAnnotations> getCompilerAnnotationsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCompilerAnnotations.class);
  }

  @Override
  @NotNull
  public List<CeylonDeclaration> getDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonImportList getImportList() {
    return findChildByClass(CeylonImportList.class);
  }

  @Override
  @Nullable
  public CeylonModuleDescriptor getModuleDescriptor() {
    return findChildByClass(CeylonModuleDescriptor.class);
  }

  @Override
  @Nullable
  public CeylonPackageDescriptor getPackageDescriptor() {
    return findChildByClass(CeylonPackageDescriptor.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCompilationUnit(this);
    else super.accept(visitor);
  }

}
