// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonStringTemplateImpl extends CeylonCompositeElementImpl implements CeylonStringTemplate {

  public CeylonStringTemplateImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonExpression.class);
  }

  @Override
  @NotNull
  public List<CeylonMyStringLiteral> getMyStringLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonMyStringLiteral.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitStringTemplate(this);
    else super.accept(visitor);
  }

}
