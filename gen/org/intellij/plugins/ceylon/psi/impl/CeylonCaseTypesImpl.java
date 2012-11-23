// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCaseTypesImpl extends CeylonCompositeElementImpl implements CeylonCaseTypes {

  public CeylonCaseTypesImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonCaseType> getCaseTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCaseType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCaseTypes(this);
    else super.accept(visitor);
  }

}
