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

public class CeylonTypeNameWithArgumentsImpl extends CeylonCompositeElementImpl implements CeylonTypeNameWithArguments {

  public CeylonTypeNameWithArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypeNameWithArguments(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonTypeArguments getTypeArguments() {
    return findChildByClass(CeylonTypeArguments.class);
  }

  @Override
  @NotNull
  public CeylonTypeName getTypeName() {
    return findNotNullChildByClass(CeylonTypeName.class);
  }

}
