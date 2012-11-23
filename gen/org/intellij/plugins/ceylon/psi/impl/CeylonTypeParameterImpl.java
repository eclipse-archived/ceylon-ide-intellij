// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTypeParameterImpl extends CeylonCompositeElementImpl implements CeylonTypeParameter {

  public CeylonTypeParameterImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonTypeNameDeclaration getTypeNameDeclaration() {
    return findNotNullChildByClass(CeylonTypeNameDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonVariance getVariance() {
    return findChildByClass(CeylonVariance.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypeParameter(this);
    else super.accept(visitor);
  }

}
