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

public class CeylonImportElementImpl extends CeylonCompositeElementImpl implements CeylonImportElement {

  public CeylonImportElementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonCompilerAnnotations getCompilerAnnotations() {
    return findNotNullChildByClass(CeylonCompilerAnnotations.class);
  }

  @Override
  @Nullable
  public CeylonImportElementList getImportElementList() {
    return findChildByClass(CeylonImportElementList.class);
  }

  @Override
  @NotNull
  public List<CeylonImportName> getImportNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonImportName.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportElement(this);
    else super.accept(visitor);
  }

}
