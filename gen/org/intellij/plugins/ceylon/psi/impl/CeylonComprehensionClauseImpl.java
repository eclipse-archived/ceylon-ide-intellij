// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonComprehensionClauseImpl extends CeylonCompositeElementImpl implements CeylonComprehensionClause {

  public CeylonComprehensionClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComprehensionClause(this);
    else super.accept(visitor);
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

}
