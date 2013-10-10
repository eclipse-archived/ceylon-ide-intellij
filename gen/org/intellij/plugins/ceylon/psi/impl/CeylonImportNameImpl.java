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

public class CeylonImportNameImpl extends CeylonCompositeElementImpl implements CeylonImportName {

  public CeylonImportNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportName(this);
    else super.accept(visitor);
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

}
