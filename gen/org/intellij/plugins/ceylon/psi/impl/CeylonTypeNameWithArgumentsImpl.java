// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTypeNameWithArgumentsImpl extends CeylonCompositeElementImpl implements CeylonTypeNameWithArguments {

  public CeylonTypeNameWithArgumentsImpl(ASTNode node) {
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
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypeNameWithArguments(this);
    else super.accept(visitor);
  }

}
