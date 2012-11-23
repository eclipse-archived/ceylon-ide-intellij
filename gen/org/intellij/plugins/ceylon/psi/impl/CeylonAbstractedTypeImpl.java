// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonAbstractedTypeImpl extends CeylonCompositeElementImpl implements CeylonAbstractedType {

  public CeylonAbstractedTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonQualifiedType getQualifiedType() {
    return findNotNullChildByClass(CeylonQualifiedType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAbstractedType(this);
    else super.accept(visitor);
  }

}
