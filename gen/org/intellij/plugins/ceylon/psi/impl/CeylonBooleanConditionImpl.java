// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonBooleanConditionImpl extends CeylonCompositeElementImpl implements CeylonBooleanCondition {

  public CeylonBooleanConditionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonExpression getExpression() {
    return findNotNullChildByClass(CeylonExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitBooleanCondition(this);
    else super.accept(visitor);
  }

}
