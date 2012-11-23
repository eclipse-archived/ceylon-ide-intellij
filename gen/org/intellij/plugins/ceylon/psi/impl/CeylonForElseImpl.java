// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonForElseImpl extends CeylonCompositeElementImpl implements CeylonForElse {

  public CeylonForElseImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonFailBlock getFailBlock() {
    return findChildByClass(CeylonFailBlock.class);
  }

  @Override
  @NotNull
  public CeylonForBlock getForBlock() {
    return findNotNullChildByClass(CeylonForBlock.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitForElse(this);
    else super.accept(visitor);
  }

}
