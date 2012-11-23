// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonStatementImpl extends CeylonCompositeElementImpl implements CeylonStatement {

  public CeylonStatementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonControlStatement getControlStatement() {
    return findChildByClass(CeylonControlStatement.class);
  }

  @Override
  @Nullable
  public CeylonDirectiveStatement getDirectiveStatement() {
    return findChildByClass(CeylonDirectiveStatement.class);
  }

  @Override
  @Nullable
  public CeylonExpressionOrSpecificationStatement getExpressionOrSpecificationStatement() {
    return findChildByClass(CeylonExpressionOrSpecificationStatement.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitStatement(this);
    else super.accept(visitor);
  }

}
