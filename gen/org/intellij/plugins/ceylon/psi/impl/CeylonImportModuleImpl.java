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

public class CeylonImportModuleImpl extends CeylonCompositeElementImpl implements CeylonImportModule {

  public CeylonImportModuleImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportModule(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonModuleVersion getModuleVersion() {
    return findNotNullChildByClass(CeylonModuleVersion.class);
  }

  @Override
  @Nullable
  public CeylonPackagePath getPackagePath() {
    return findChildByClass(CeylonPackagePath.class);
  }

}
