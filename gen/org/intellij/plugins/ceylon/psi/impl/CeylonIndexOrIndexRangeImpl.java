// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIndexOrIndexRangeImpl extends CeylonCompositeElementImpl implements CeylonIndexOrIndexRange {

  public CeylonIndexOrIndexRangeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonElementSelectionOperator getElementSelectionOperator() {
    return findNotNullChildByClass(CeylonElementSelectionOperator.class);
  }

  @Override
  @NotNull
  public List<CeylonIndex> getIndexList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonIndex.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIndexOrIndexRange(this);
    else super.accept(visitor);
  }

}
