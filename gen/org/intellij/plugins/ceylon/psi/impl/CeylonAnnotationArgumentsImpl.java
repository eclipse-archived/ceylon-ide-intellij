// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonAnnotationArgumentsImpl extends CeylonCompositeElementImpl implements CeylonAnnotationArguments {

  public CeylonAnnotationArgumentsImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonArguments getArguments() {
    return findChildByClass(CeylonArguments.class);
  }

  @Override
  @Nullable
  public CeylonLiteralArguments getLiteralArguments() {
    return findChildByClass(CeylonLiteralArguments.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAnnotationArguments(this);
    else super.accept(visitor);
  }

}
