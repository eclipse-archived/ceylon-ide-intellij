// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonParameterTypeImpl extends CeylonCompositeElementImpl implements CeylonParameterType {

  public CeylonParameterTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonType getType() {
    return findChildByClass(CeylonType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitParameterType(this);
    else super.accept(visitor);
  }

}
