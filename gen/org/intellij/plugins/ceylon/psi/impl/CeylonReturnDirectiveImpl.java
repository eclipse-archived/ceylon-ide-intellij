// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonReturnDirectiveImpl extends CeylonCompositeElementImpl implements CeylonReturnDirective {

  public CeylonReturnDirectiveImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonFunctionOrExpression getFunctionOrExpression() {
    return findChildByClass(CeylonFunctionOrExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitReturnDirective(this);
    else super.accept(visitor);
  }

}
