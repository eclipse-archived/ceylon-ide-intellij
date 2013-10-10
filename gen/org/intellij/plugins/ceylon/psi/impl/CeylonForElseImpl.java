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

public class CeylonForElseImpl extends CeylonCompositeElementImpl implements CeylonForElse {

  public CeylonForElseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitForElse(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonFailBlock getFailBlock() {
    return findChildByClass(CeylonFailBlock.class);
  }

  @Override
  @NotNull
  public CeylonForBlock getForBlock() {
    return findNotNullChildByClass(CeylonForBlock.class);
  }

}
