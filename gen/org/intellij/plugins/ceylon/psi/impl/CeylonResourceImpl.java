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

public class CeylonResourceImpl extends CeylonCompositeElementImpl implements CeylonResource {

  public CeylonResourceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitResource(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonExpression getExpression() {
    return findChildByClass(CeylonExpression.class);
  }

  @Override
  @Nullable
  public CeylonSpecifiedVariable getSpecifiedVariable() {
    return findChildByClass(CeylonSpecifiedVariable.class);
  }

}
