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

public class CeylonIncrementDecrementExpressionImpl extends CeylonCompositeElementImpl implements CeylonIncrementDecrementExpression {

  public CeylonIncrementDecrementExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIncrementDecrementExpression(this);
    else super.accept(visitor);
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

}
