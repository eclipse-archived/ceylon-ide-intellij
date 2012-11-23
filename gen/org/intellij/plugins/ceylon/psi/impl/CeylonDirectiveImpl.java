// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonDirectiveImpl extends CeylonCompositeElementImpl implements CeylonDirective {

  public CeylonDirectiveImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBreakDirective getBreakDirective() {
    return findChildByClass(CeylonBreakDirective.class);
  }

  @Override
  @Nullable
  public CeylonContinueDirective getContinueDirective() {
    return findChildByClass(CeylonContinueDirective.class);
  }

  @Override
  @Nullable
  public CeylonReturnDirective getReturnDirective() {
    return findChildByClass(CeylonReturnDirective.class);
  }

  @Override
  @Nullable
  public CeylonThrowDirective getThrowDirective() {
    return findChildByClass(CeylonThrowDirective.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitDirective(this);
    else super.accept(visitor);
  }

}
