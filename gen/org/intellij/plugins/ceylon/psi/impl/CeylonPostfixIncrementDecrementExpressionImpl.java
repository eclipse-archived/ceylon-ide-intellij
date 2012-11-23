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

public class CeylonPostfixIncrementDecrementExpressionImpl extends CeylonCompositeElementImpl implements CeylonPostfixIncrementDecrementExpression {

  public CeylonPostfixIncrementDecrementExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonPostfixOperator> getPostfixOperatorList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonPostfixOperator.class);
  }

  @Override
  @NotNull
  public CeylonPrimary getPrimary() {
    return findNotNullChildByClass(CeylonPrimary.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitPostfixIncrementDecrementExpression(this);
    else super.accept(visitor);
  }

}
