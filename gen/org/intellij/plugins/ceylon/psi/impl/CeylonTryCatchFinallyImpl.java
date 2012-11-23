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

public class CeylonTryCatchFinallyImpl extends CeylonCompositeElementImpl implements CeylonTryCatchFinally {

  public CeylonTryCatchFinallyImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonCatchBlock> getCatchBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCatchBlock.class);
  }

  @Override
  @Nullable
  public CeylonFinallyBlock getFinallyBlock() {
    return findChildByClass(CeylonFinallyBlock.class);
  }

  @Override
  @NotNull
  public CeylonTryBlock getTryBlock() {
    return findNotNullChildByClass(CeylonTryBlock.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTryCatchFinally(this);
    else super.accept(visitor);
  }

}
