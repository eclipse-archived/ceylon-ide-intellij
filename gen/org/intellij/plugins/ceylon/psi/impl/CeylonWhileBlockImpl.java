// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonWhileBlockImpl extends CeylonCompositeElementImpl implements CeylonWhileBlock {

  public CeylonWhileBlockImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonConditions getConditions() {
    return findNotNullChildByClass(CeylonConditions.class);
  }

  @Override
  @NotNull
  public CeylonControlBlock getControlBlock() {
    return findNotNullChildByClass(CeylonControlBlock.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitWhileBlock(this);
    else super.accept(visitor);
  }

}
