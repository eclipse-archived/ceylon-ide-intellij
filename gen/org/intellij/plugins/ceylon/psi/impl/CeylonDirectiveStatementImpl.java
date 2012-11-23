// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonDirectiveStatementImpl extends CeylonCompositeElementImpl implements CeylonDirectiveStatement {

  public CeylonDirectiveStatementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonDirective getDirective() {
    return findNotNullChildByClass(CeylonDirective.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitDirectiveStatement(this);
    else super.accept(visitor);
  }

}
