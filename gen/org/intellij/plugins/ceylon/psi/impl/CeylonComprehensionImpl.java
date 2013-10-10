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

public class CeylonComprehensionImpl extends CeylonCompositeElementImpl implements CeylonComprehension {

  public CeylonComprehensionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitComprehension(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonForComprehensionClause getForComprehensionClause() {
    return findNotNullChildByClass(CeylonForComprehensionClause.class);
  }

}
