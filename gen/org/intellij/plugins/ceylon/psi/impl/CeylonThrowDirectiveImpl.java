// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonThrowDirectiveImpl extends CeylonCompositeElementImpl implements CeylonThrowDirective {

  public CeylonThrowDirectiveImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExpression getExpression() {
    return findChildByClass(CeylonExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitThrowDirective(this);
    else super.accept(visitor);
  }

}
