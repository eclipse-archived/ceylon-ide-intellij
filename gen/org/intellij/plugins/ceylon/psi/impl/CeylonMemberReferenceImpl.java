// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonMemberReferenceImpl extends CeylonCompositeElementImpl implements CeylonMemberReference {

  public CeylonMemberReferenceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonMemberName getMemberName() {
    return findNotNullChildByClass(CeylonMemberName.class);
  }

  @Override
  @Nullable
  public CeylonTypeArguments getTypeArguments() {
    return findChildByClass(CeylonTypeArguments.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitMemberReference(this);
    else super.accept(visitor);
  }

}
