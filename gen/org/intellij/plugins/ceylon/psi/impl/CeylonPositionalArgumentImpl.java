// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonPositionalArgumentImpl extends CeylonCompositeElementImpl implements CeylonPositionalArgument {

  public CeylonPositionalArgumentImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonFunctionOrExpression getFunctionOrExpression() {
    return findNotNullChildByClass(CeylonFunctionOrExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitPositionalArgument(this);
    else super.accept(visitor);
  }

}
