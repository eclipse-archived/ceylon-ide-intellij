// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCatchVariableImpl extends CeylonCompositeElementImpl implements CeylonCatchVariable {

  public CeylonCatchVariableImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonVariable getVariable() {
    return findChildByClass(CeylonVariable.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCatchVariable(this);
    else super.accept(visitor);
  }

}
