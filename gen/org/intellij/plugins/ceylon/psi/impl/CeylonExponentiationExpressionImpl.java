// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonExponentiationExpressionImpl extends CeylonCompositeElementImpl implements CeylonExponentiationExpression {

  public CeylonExponentiationExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExponentiationExpression getExponentiationExpression() {
    return findChildByClass(CeylonExponentiationExpression.class);
  }

  @Override
  @Nullable
  public CeylonExponentiationOperator getExponentiationOperator() {
    return findChildByClass(CeylonExponentiationOperator.class);
  }

  @Override
  @NotNull
  public CeylonIncrementDecrementExpression getIncrementDecrementExpression() {
    return findNotNullChildByClass(CeylonIncrementDecrementExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExponentiationExpression(this);
    else super.accept(visitor);
  }

}
