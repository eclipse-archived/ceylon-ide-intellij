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

public class CeylonParameterTypeImpl extends CeylonCompositeElementImpl implements CeylonParameterType {

  public CeylonParameterTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonType getType() {
    return findChildByClass(CeylonType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitParameterType(this);
    else super.accept(visitor);
  }

}
