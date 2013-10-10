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

public class CeylonThenElseExpressionImpl extends CeylonCompositeElementImpl implements CeylonThenElseExpression {

  public CeylonThenElseExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitThenElseExpression(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CeylonDisjunctionExpression> getDisjunctionExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonDisjunctionExpression.class);
  }

  @Override
  @NotNull
  public List<CeylonThenElseOperator> getThenElseOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonThenElseOperator.class);
  }

}
