// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonUnaryMinusOrComplementOperatorImpl extends CeylonCompositeElementImpl implements CeylonUnaryMinusOrComplementOperator {

  public CeylonUnaryMinusOrComplementOperatorImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitUnaryMinusOrComplementOperator(this);
    else super.accept(visitor);
  }

}
