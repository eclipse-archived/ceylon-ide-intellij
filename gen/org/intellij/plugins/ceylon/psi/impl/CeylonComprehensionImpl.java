// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonComprehensionImpl extends CeylonCompositeElementImpl implements CeylonComprehension {

  public CeylonComprehensionImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonForComprehensionClause getForComprehensionClause() {
    return findNotNullChildByClass(CeylonForComprehensionClause.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComprehension(this);
    else super.accept(visitor);
  }

}
