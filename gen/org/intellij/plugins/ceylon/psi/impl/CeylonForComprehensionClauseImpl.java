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

public class CeylonForComprehensionClauseImpl extends CeylonCompositeElementImpl implements CeylonForComprehensionClause {

  public CeylonForComprehensionClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitForComprehensionClause(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonComprehensionClause getComprehensionClause() {
    return findNotNullChildByClass(CeylonComprehensionClause.class);
  }

  @Override
  @NotNull
  public CeylonForIterator getForIterator() {
    return findNotNullChildByClass(CeylonForIterator.class);
  }

}
