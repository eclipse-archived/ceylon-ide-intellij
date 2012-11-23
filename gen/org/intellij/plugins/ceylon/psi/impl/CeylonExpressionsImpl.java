// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonExpressionsImpl extends CeylonCompositeElementImpl implements CeylonExpressions {

  public CeylonExpressionsImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExpressions(this);
    else super.accept(visitor);
  }

}
