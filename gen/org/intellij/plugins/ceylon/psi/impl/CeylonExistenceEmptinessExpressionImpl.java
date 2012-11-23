// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonExistenceEmptinessExpressionImpl extends CeylonCompositeElementImpl implements CeylonExistenceEmptinessExpression {

  public CeylonExistenceEmptinessExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExistsNonemptyOperator getExistsNonemptyOperator() {
    return findChildByClass(CeylonExistsNonemptyOperator.class);
  }

  @Override
  @NotNull
  public CeylonRangeIntervalEntryExpression getRangeIntervalEntryExpression() {
    return findNotNullChildByClass(CeylonRangeIntervalEntryExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExistenceEmptinessExpression(this);
    else super.accept(visitor);
  }

}
