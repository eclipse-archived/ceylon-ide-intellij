// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonComparableTypeImpl extends CeylonCompositeElementImpl implements CeylonComparableType {

  public CeylonComparableTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonUnionTypeExpression getUnionTypeExpression() {
    return findNotNullChildByClass(CeylonUnionTypeExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComparableType(this);
    else super.accept(visitor);
  }

}
