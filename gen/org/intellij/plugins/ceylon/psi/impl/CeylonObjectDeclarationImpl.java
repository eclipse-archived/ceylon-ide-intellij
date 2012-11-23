// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonObjectDeclarationImpl extends CeylonCompositeElementImpl implements CeylonObjectDeclaration {

  public CeylonObjectDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonClassBody getClassBody() {
    return findChildByClass(CeylonClassBody.class);
  }

  @Override
  @Nullable
  public CeylonExtendedType getExtendedType() {
    return findChildByClass(CeylonExtendedType.class);
  }

  @Override
  @NotNull
  public CeylonMemberNameDeclaration getMemberNameDeclaration() {
    return findNotNullChildByClass(CeylonMemberNameDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonSatisfiedTypes getSatisfiedTypes() {
    return findChildByClass(CeylonSatisfiedTypes.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitObjectDeclaration(this);
    else super.accept(visitor);
  }

}
