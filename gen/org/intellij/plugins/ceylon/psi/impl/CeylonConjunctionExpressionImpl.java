// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonConjunctionExpressionImpl extends CeylonCompositeElementImpl implements CeylonConjunctionExpression {

  public CeylonConjunctionExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonConjunctionOperator> getConjunctionOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonConjunctionOperator.class);
  }

  @Override
  @NotNull
  public List<CeylonLogicalNegationExpression> getLogicalNegationExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonLogicalNegationExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitConjunctionExpression(this);
    else super.accept(visitor);
  }

}
