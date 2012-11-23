// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonAssertionImpl extends CeylonCompositeElementImpl implements CeylonAssertion {

  public CeylonAssertionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonConditions getConditions() {
    return findNotNullChildByClass(CeylonConditions.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAssertion(this);
    else super.accept(visitor);
  }

}
