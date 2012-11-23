// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonModuleDescriptorImpl extends CeylonCompositeElementImpl implements CeylonModuleDescriptor {

  public CeylonModuleDescriptorImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonImportModuleList getImportModuleList() {
    return findNotNullChildByClass(CeylonImportModuleList.class);
  }

  @Override
  @NotNull
  public CeylonPackagePath getPackagePath() {
    return findNotNullChildByClass(CeylonPackagePath.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitModuleDescriptor(this);
    else super.accept(visitor);
  }

}
