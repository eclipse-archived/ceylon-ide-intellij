// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonEqualityExpressionImpl extends CeylonCompositeElementImpl implements CeylonEqualityExpression {

  public CeylonEqualityExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonComparisonExpression> getComparisonExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonComparisonExpression.class);
  }

  @Override
  @Nullable
  public CeylonEqualityOperator getEqualityOperator() {
    return findChildByClass(CeylonEqualityOperator.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitEqualityExpression(this);
    else super.accept(visitor);
  }

}
