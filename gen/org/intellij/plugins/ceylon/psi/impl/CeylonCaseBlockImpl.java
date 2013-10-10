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

public class CeylonCaseBlockImpl extends CeylonCompositeElementImpl implements CeylonCaseBlock {

  public CeylonCaseBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCaseBlock(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonBlock getBlock() {
    return findNotNullChildByClass(CeylonBlock.class);
  }

  @Override
  @Nullable
  public CeylonCaseItem getCaseItem() {
    return findChildByClass(CeylonCaseItem.class);
  }

}
