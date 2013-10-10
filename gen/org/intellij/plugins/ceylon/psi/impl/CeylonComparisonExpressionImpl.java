// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonComparisonExpressionImpl extends CeylonCompositeElementImpl implements CeylonComparisonExpression {

  public CeylonComparisonExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComparisonExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonComparableType getComparableType() {
    return findChildByClass(CeylonComparableType.class);
  }

  @Override
  @Nullable
  public CeylonComparisonOperator getComparisonOperator() {
    return findChildByClass(CeylonComparisonOperator.class);
  }

  @Override
  @NotNull
  public List<CeylonExistenceEmptinessExpression> getExistenceEmptinessExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonExistenceEmptinessExpression.class);
  }

  @Override
  @Nullable
  public CeylonType getType() {
    return findChildByClass(CeylonType.class);
  }

  @Override
  @Nullable
  public CeylonTypeOperator getTypeOperator() {
    return findChildByClass(CeylonTypeOperator.class);
  }

}
