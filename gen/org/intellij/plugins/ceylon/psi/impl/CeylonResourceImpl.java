// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonResourceImpl extends CeylonCompositeElementImpl implements CeylonResource {

  public CeylonResourceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExpression getExpression() {
    return findChildByClass(CeylonExpression.class);
  }

  @Override
  @Nullable
  public CeylonSpecifiedVariable getSpecifiedVariable() {
    return findChildByClass(CeylonSpecifiedVariable.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitResource(this);
    else super.accept(visitor);
  }

}
