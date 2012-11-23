// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonAnnotationImpl extends CeylonCompositeElementImpl implements CeylonAnnotation {

  public CeylonAnnotationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonAnnotationArguments getAnnotationArguments() {
    return findNotNullChildByClass(CeylonAnnotationArguments.class);
  }

  @Override
  @NotNull
  public CeylonAnnotationName getAnnotationName() {
    return findNotNullChildByClass(CeylonAnnotationName.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAnnotation(this);
    else super.accept(visitor);
  }

}
