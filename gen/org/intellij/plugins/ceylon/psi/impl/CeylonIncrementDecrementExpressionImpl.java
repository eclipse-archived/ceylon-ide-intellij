// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIncrementDecrementExpressionImpl extends CeylonCompositeElementImpl implements CeylonIncrementDecrementExpression {

  public CeylonIncrementDecrementExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonIncrementDecrementExpression getIncrementDecrementExpression() {
    return findChildByClass(CeylonIncrementDecrementExpression.class);
  }

  @Override
  @Nullable
  public CeylonPostfixIncrementDecrementExpression getPostfixIncrementDecrementExpression() {
    return findChildByClass(CeylonPostfixIncrementDecrementExpression.class);
  }

  @Override
  @Nullable
  public CeylonPrefixOperator getPrefixOperator() {
    return findChildByClass(CeylonPrefixOperator.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIncrementDecrementExpression(this);
    else super.accept(visitor);
  }

}
