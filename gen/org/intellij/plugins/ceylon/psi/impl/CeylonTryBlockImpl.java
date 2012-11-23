// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTryBlockImpl extends CeylonCompositeElementImpl implements CeylonTryBlock {

  public CeylonTryBlockImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBlock getBlock() {
    return findChildByClass(CeylonBlock.class);
  }

  @Override
  @Nullable
  public CeylonControlBlock getControlBlock() {
    return findChildByClass(CeylonControlBlock.class);
  }

  @Override
  @Nullable
  public CeylonResource getResource() {
    return findChildByClass(CeylonResource.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTryBlock(this);
    else super.accept(visitor);
  }

}
