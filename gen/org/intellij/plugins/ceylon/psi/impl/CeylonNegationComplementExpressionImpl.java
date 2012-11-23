// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonNegationComplementExpressionImpl extends CeylonCompositeElementImpl implements CeylonNegationComplementExpression {

  public CeylonNegationComplementExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExponentiationExpression getExponentiationExpression() {
    return findChildByClass(CeylonExponentiationExpression.class);
  }

  @Override
  @Nullable
  public CeylonNegationComplementExpression getNegationComplementExpression() {
    return findChildByClass(CeylonNegationComplementExpression.class);
  }

  @Override
  @Nullable
  public CeylonUnaryMinusOrComplementOperator getUnaryMinusOrComplementOperator() {
    return findChildByClass(CeylonUnaryMinusOrComplementOperator.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitNegationComplementExpression(this);
    else super.accept(visitor);
  }

}
