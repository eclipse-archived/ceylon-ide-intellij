// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonImportNameImpl extends CeylonCompositeElementImpl implements CeylonImportName {

  public CeylonImportNameImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonMemberName getMemberName() {
    return findChildByClass(CeylonMemberName.class);
  }

  @Override
  @Nullable
  public CeylonTypeName getTypeName() {
    return findChildByClass(CeylonTypeName.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportName(this);
    else super.accept(visitor);
  }

}
