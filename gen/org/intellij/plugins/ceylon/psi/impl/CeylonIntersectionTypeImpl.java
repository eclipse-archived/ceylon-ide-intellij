// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonIntersectionTypeImpl extends CeylonCompositeElementImpl implements CeylonIntersectionType {

  public CeylonIntersectionTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonEntryType> getEntryTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonEntryType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitIntersectionType(this);
    else super.accept(visitor);
  }

}
