// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonLogicalNegationExpressionImpl extends CeylonCompositeElementImpl implements CeylonLogicalNegationExpression {

  public CeylonLogicalNegationExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonEqualityExpression getEqualityExpression() {
    return findChildByClass(CeylonEqualityExpression.class);
  }

  @Override
  @Nullable
  public CeylonLogicalNegationExpression getLogicalNegationExpression() {
    return findChildByClass(CeylonLogicalNegationExpression.class);
  }

  @Override
  @Nullable
  public CeylonNotOperator getNotOperator() {
    return findChildByClass(CeylonNotOperator.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitLogicalNegationExpression(this);
    else super.accept(visitor);
  }

}
