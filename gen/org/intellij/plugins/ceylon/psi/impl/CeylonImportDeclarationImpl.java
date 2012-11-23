// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonImportDeclarationImpl extends CeylonCompositeElementImpl implements CeylonImportDeclaration {

  public CeylonImportDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonImportElementList getImportElementList() {
    return findNotNullChildByClass(CeylonImportElementList.class);
  }

  @Override
  @NotNull
  public CeylonPackagePath getPackagePath() {
    return findNotNullChildByClass(CeylonPackagePath.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportDeclaration(this);
    else super.accept(visitor);
  }

}
