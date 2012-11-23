// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonParExpressionImpl extends CeylonCompositeElementImpl implements CeylonParExpression {

  public CeylonParExpressionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonAssignmentExpression getAssignmentExpression() {
    return findNotNullChildByClass(CeylonAssignmentExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitParExpression(this);
    else super.accept(visitor);
  }

}
