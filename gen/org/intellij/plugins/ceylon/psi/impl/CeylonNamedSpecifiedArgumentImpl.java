// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonNamedSpecifiedArgumentImpl extends CeylonCompositeElementImpl implements CeylonNamedSpecifiedArgument {

  public CeylonNamedSpecifiedArgumentImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonMemberNameDeclaration getMemberNameDeclaration() {
    return findNotNullChildByClass(CeylonMemberNameDeclaration.class);
  }

  @Override
  @NotNull
  public CeylonSpecifier getSpecifier() {
    return findNotNullChildByClass(CeylonSpecifier.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitNamedSpecifiedArgument(this);
    else super.accept(visitor);
  }

}
