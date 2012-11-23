// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCasesImpl extends CeylonCompositeElementImpl implements CeylonCases {

  public CeylonCasesImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonCaseBlock> getCaseBlockList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCaseBlock.class);
  }

  @Override
  @Nullable
  public CeylonDefaultCaseBlock getDefaultCaseBlock() {
    return findChildByClass(CeylonDefaultCaseBlock.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCases(this);
    else super.accept(visitor);
  }

}
