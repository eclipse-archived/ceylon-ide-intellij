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

public class CeylonCatchBlockImpl extends CeylonCompositeElementImpl implements CeylonCatchBlock {

  public CeylonCatchBlockImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonBlock getBlock() {
    return findNotNullChildByClass(CeylonBlock.class);
  }

  @Override
  @NotNull
  public CeylonCatchVariable getCatchVariable() {
    return findNotNullChildByClass(CeylonCatchVariable.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCatchBlock(this);
    else super.accept(visitor);
  }

}
