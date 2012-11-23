// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonConditionImpl extends CeylonCompositeElementImpl implements CeylonCondition {

  public CeylonConditionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBooleanCondition getBooleanCondition() {
    return findChildByClass(CeylonBooleanCondition.class);
  }

  @Override
  @Nullable
  public CeylonExistsCondition getExistsCondition() {
    return findChildByClass(CeylonExistsCondition.class);
  }

  @Override
  @Nullable
  public CeylonIsCondition getIsCondition() {
    return findChildByClass(CeylonIsCondition.class);
  }

  @Override
  @Nullable
  public CeylonNonemptyCondition getNonemptyCondition() {
    return findChildByClass(CeylonNonemptyCondition.class);
  }

  @Override
  @Nullable
  public CeylonSatisfiesCondition getSatisfiesCondition() {
    return findChildByClass(CeylonSatisfiesCondition.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCondition(this);
    else super.accept(visitor);
  }

}
