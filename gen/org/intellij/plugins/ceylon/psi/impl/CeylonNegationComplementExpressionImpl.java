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

public class CeylonNegationComplementExpressionImpl extends CeylonCompositeElementImpl implements CeylonNegationComplementExpression {

  public CeylonNegationComplementExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitNegationComplementExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonExponentiationExpression getExponentiationExpression() {
    return findChildByClass(CeylonExponentiationExpression.class);
  }

  @Override
  @Nullable
  public CeylonNegationComplementExpression getNegationComplementExpression() {
    return findChildByClass(CeylonNegationComplementExpression.class);
  }

  @Override
  @Nullable
  public CeylonUnaryMinusOrComplementOperator getUnaryMinusOrComplementOperator() {
    return findChildByClass(CeylonUnaryMinusOrComplementOperator.class);
  }

}
