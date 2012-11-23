// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonImportModuleImpl extends CeylonCompositeElementImpl implements CeylonImportModule {

  public CeylonImportModuleImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonPackagePath getPackagePath() {
    return findChildByClass(CeylonPackagePath.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportModule(this);
    else super.accept(visitor);
  }

}
