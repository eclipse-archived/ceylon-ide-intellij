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

public class CeylonAnnotationArgumentsImpl extends CeylonCompositeElementImpl implements CeylonAnnotationArguments {

  public CeylonAnnotationArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAnnotationArguments(this);
    else super.accept(visitor);
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

}
