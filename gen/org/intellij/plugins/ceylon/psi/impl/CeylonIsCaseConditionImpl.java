// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIsCaseConditionImpl extends CeylonCompositeElementImpl implements CeylonIsCaseCondition {

  public CeylonIsCaseConditionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonType getType() {
    return findNotNullChildByClass(CeylonType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIsCaseCondition(this);
    else super.accept(visitor);
  }

}
