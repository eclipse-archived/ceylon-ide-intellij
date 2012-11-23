// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCaseItemImpl extends CeylonCompositeElementImpl implements CeylonCaseItem {

  public CeylonCaseItemImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonIsCaseCondition getIsCaseCondition() {
    return findChildByClass(CeylonIsCaseCondition.class);
  }

  @Override
  @Nullable
  public CeylonMatchCaseCondition getMatchCaseCondition() {
    return findChildByClass(CeylonMatchCaseCondition.class);
  }

  @Override
  @Nullable
  public CeylonSatisfiesCaseCondition getSatisfiesCaseCondition() {
    return findChildByClass(CeylonSatisfiesCaseCondition.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCaseItem(this);
    else super.accept(visitor);
  }

}
