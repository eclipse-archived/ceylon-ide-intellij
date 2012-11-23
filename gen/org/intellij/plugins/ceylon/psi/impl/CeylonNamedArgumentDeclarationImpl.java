// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonNamedArgumentDeclarationImpl extends CeylonCompositeElementImpl implements CeylonNamedArgumentDeclaration {

  public CeylonNamedArgumentDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonInferredGetterArgument getInferredGetterArgument() {
    return findChildByClass(CeylonInferredGetterArgument.class);
  }

  @Override
  @Nullable
  public CeylonObjectArgument getObjectArgument() {
    return findChildByClass(CeylonObjectArgument.class);
  }

  @Override
  @Nullable
  public CeylonTypedMethodOrGetterArgument getTypedMethodOrGetterArgument() {
    return findChildByClass(CeylonTypedMethodOrGetterArgument.class);
  }

  @Override
  @Nullable
  public CeylonVoidOrInferredMethodArgument getVoidOrInferredMethodArgument() {
    return findChildByClass(CeylonVoidOrInferredMethodArgument.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitNamedArgumentDeclaration(this);
    else super.accept(visitor);
  }

}
