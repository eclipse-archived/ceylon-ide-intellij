// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonPackageDescriptorImpl extends CeylonCompositeElementImpl implements CeylonPackageDescriptor {

  public CeylonPackageDescriptorImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonPackagePath getPackagePath() {
    return findNotNullChildByClass(CeylonPackagePath.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitPackageDescriptor(this);
    else super.accept(visitor);
  }

}
