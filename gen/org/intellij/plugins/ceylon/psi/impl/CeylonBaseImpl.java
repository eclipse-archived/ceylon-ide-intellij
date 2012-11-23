// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonBaseImpl extends CeylonCompositeElementImpl implements CeylonBase {

  public CeylonBaseImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBaseReference getBaseReference() {
    return findChildByClass(CeylonBaseReference.class);
  }

  @Override
  @Nullable
  public CeylonEnumeration getEnumeration() {
    return findChildByClass(CeylonEnumeration.class);
  }

  @Override
  @Nullable
  public CeylonNonstringLiteral getNonstringLiteral() {
    return findChildByClass(CeylonNonstringLiteral.class);
  }

  @Override
  @Nullable
  public CeylonParExpression getParExpression() {
    return findChildByClass(CeylonParExpression.class);
  }

  @Override
  @Nullable
  public CeylonSelfReference getSelfReference() {
    return findChildByClass(CeylonSelfReference.class);
  }

  @Override
  @Nullable
  public CeylonStringExpression getStringExpression() {
    return findChildByClass(CeylonStringExpression.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitBase(this);
    else super.accept(visitor);
  }

}
