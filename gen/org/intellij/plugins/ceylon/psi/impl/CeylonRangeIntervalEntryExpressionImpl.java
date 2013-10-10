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

public class CeylonRangeIntervalEntryExpressionImpl extends CeylonCompositeElementImpl implements CeylonRangeIntervalEntryExpression {

  public CeylonRangeIntervalEntryExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitRangeIntervalEntryExpression(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CeylonAdditiveExpression> getAdditiveExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonAdditiveExpression.class);
  }

  @Override
  @Nullable
  public CeylonRangeIntervalEntryOperator getRangeIntervalEntryOperator() {
    return findChildByClass(CeylonRangeIntervalEntryOperator.class);
  }

}
