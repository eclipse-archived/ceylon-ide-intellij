// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonExtendedTypeImpl extends CeylonCompositeElementImpl implements CeylonExtendedType {

  public CeylonExtendedTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonPositionalArguments getPositionalArguments() {
    return findChildByClass(CeylonPositionalArguments.class);
  }

  @Override
  @Nullable
  public CeylonQualifiedType getQualifiedType() {
    return findChildByClass(CeylonQualifiedType.class);
  }

  @Override
  @Nullable
  public CeylonTypeReference getTypeReference() {
    return findChildByClass(CeylonTypeReference.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExtendedType(this);
    else super.accept(visitor);
  }

}
