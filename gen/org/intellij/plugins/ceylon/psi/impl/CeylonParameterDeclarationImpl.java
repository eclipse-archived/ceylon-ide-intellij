// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonParameterDeclarationImpl extends CeylonCompositeElementImpl implements CeylonParameterDeclaration {

  public CeylonParameterDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonAnnotations getAnnotations() {
    return findChildByClass(CeylonAnnotations.class);
  }

  @Override
  @NotNull
  public CeylonCompilerAnnotations getCompilerAnnotations() {
    return findNotNullChildByClass(CeylonCompilerAnnotations.class);
  }

  @Override
  @Nullable
  public CeylonParameter getParameter() {
    return findChildByClass(CeylonParameter.class);
  }

  @Override
  @Nullable
  public CeylonParameterRef getParameterRef() {
    return findChildByClass(CeylonParameterRef.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitParameterDeclaration(this);
    else super.accept(visitor);
  }

}
