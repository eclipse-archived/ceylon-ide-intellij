// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIsConditionImpl extends CeylonCompositeElementImpl implements CeylonIsCondition {

  public CeylonIsConditionImpl(ASTNode node) {
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
  public CeylonMemberName getMemberName() {
    return findChildByClass(CeylonMemberName.class);
  }

  @Override
  @Nullable
  public CeylonSpecifier getSpecifier() {
    return findChildByClass(CeylonSpecifier.class);
  }

  @Override
  @Nullable
  public CeylonType getType() {
    return findChildByClass(CeylonType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIsCondition(this);
    else super.accept(visitor);
  }

}
