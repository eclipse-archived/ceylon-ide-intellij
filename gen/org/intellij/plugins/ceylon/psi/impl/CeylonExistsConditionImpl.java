// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonExistsConditionImpl extends CeylonCompositeElementImpl implements CeylonExistsCondition {

  public CeylonExistsConditionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBooleanCondition getBooleanCondition() {
    return findChildByClass(CeylonBooleanCondition.class);
  }

  @Override
  @Nullable
  public CeylonImpliedVariable getImpliedVariable() {
    return findChildByClass(CeylonImpliedVariable.class);
  }

  @Override
  @Nullable
  public CeylonSpecifiedVariable getSpecifiedVariable() {
    return findChildByClass(CeylonSpecifiedVariable.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExistsCondition(this);
    else super.accept(visitor);
  }

}
