// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTypeImpl extends CeylonCompositeElementImpl implements CeylonType {

  public CeylonTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonUnionType getUnionType() {
    return findNotNullChildByClass(CeylonUnionType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitType(this);
    else super.accept(visitor);
  }

}
