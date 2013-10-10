// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIsConditionImpl extends CeylonCompositeElementImpl implements CeylonIsCondition {

  public CeylonIsConditionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIsCondition(this);
    else super.accept(visitor);
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

}
