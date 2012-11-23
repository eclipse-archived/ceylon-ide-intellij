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

public class CeylonCompilerAnnotationImpl extends CeylonCompositeElementImpl implements CeylonCompilerAnnotation {

  public CeylonCompilerAnnotationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonAnnotationName getAnnotationName() {
    return findNotNullChildByClass(CeylonAnnotationName.class);
  }

  @Override
  @Nullable
  public CeylonMyStringLiteral getMyStringLiteral() {
    return findChildByClass(CeylonMyStringLiteral.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCompilerAnnotation(this);
    else super.accept(visitor);
  }

}
