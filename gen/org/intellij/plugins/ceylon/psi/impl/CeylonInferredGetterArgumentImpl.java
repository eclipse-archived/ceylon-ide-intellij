// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonInferredGetterArgumentImpl extends CeylonCompositeElementImpl implements CeylonInferredGetterArgument {

  public CeylonInferredGetterArgumentImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonBlock getBlock() {
    return findNotNullChildByClass(CeylonBlock.class);
  }

  @Override
  @NotNull
  public CeylonMemberNameDeclaration getMemberNameDeclaration() {
    return findNotNullChildByClass(CeylonMemberNameDeclaration.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitInferredGetterArgument(this);
    else super.accept(visitor);
  }

}
