// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIfComprehensionClauseImpl extends CeylonCompositeElementImpl implements CeylonIfComprehensionClause {

  public CeylonIfComprehensionClauseImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonComprehensionClause getComprehensionClause() {
    return findNotNullChildByClass(CeylonComprehensionClause.class);
  }

  @Override
  @NotNull
  public CeylonConditions getConditions() {
    return findNotNullChildByClass(CeylonConditions.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIfComprehensionClause(this);
    else super.accept(visitor);
  }

}
