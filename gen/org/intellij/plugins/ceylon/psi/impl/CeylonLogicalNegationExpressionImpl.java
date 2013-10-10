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

public class CeylonLogicalNegationExpressionImpl extends CeylonCompositeElementImpl implements CeylonLogicalNegationExpression {

  public CeylonLogicalNegationExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitLogicalNegationExpression(this);
    else super.accept(visitor);
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

}
