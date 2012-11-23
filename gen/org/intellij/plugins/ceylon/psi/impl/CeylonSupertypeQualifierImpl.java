// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonSupertypeQualifierImpl extends CeylonCompositeElementImpl implements CeylonSupertypeQualifier {

  public CeylonSupertypeQualifierImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonTypeName getTypeName() {
    return findNotNullChildByClass(CeylonTypeName.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitSupertypeQualifier(this);
    else super.accept(visitor);
  }

}
