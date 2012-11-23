// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonAdditiveExpressionImpl extends CeylonCompositeElementImpl implements CeylonAdditiveExpression {

  public CeylonAdditiveExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonAdditiveOperator> getAdditiveOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonAdditiveOperator.class);
  }

  @Override
  @NotNull
  public List<CeylonMultiplicativeExpression> getMultiplicativeExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonMultiplicativeExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAdditiveExpression(this);
    else super.accept(visitor);
  }

}
