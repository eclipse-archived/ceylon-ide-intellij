// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonComprehensionClauseImpl extends CeylonCompositeElementImpl implements CeylonComprehensionClause {

  public CeylonComprehensionClauseImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonExpressionComprehensionClause getExpressionComprehensionClause() {
    return findChildByClass(CeylonExpressionComprehensionClause.class);
  }

  @Override
  @Nullable
  public CeylonForComprehensionClause getForComprehensionClause() {
    return findChildByClass(CeylonForComprehensionClause.class);
  }

  @Override
  @Nullable
  public CeylonIfComprehensionClause getIfComprehensionClause() {
    return findChildByClass(CeylonIfComprehensionClause.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComprehensionClause(this);
    else super.accept(visitor);
  }

}
