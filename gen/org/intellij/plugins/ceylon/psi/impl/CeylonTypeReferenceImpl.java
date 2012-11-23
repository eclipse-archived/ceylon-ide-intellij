// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTypeReferenceImpl extends CeylonCompositeElementImpl implements CeylonTypeReference {

  public CeylonTypeReferenceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonTypeArguments getTypeArguments() {
    return findChildByClass(CeylonTypeArguments.class);
  }

  @Override
  @NotNull
  public CeylonTypeName getTypeName() {
    return findNotNullChildByClass(CeylonTypeName.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypeReference(this);
    else super.accept(visitor);
  }

}
