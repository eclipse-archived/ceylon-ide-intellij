// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIfElseImpl extends CeylonCompositeElementImpl implements CeylonIfElse {

  public CeylonIfElseImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonElseBlock getElseBlock() {
    return findChildByClass(CeylonElseBlock.class);
  }

  @Override
  @NotNull
  public CeylonIfBlock getIfBlock() {
    return findNotNullChildByClass(CeylonIfBlock.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIfElse(this);
    else super.accept(visitor);
  }

}
