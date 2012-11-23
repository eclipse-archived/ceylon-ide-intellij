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

public class CeylonPrimaryImpl extends CeylonCompositeElementImpl implements CeylonPrimary {

  public CeylonPrimaryImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonArguments> getArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonArguments.class);
  }

  @Override
  @NotNull
  public CeylonBase getBase() {
    return findNotNullChildByClass(CeylonBase.class);
  }

  @Override
  @NotNull
  public List<CeylonIndexOrIndexRange> getIndexOrIndexRangeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonIndexOrIndexRange.class);
  }

  @Override
  @NotNull
  public List<CeylonQualifiedReference> getQualifiedReferenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonQualifiedReference.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitPrimary(this);
    else super.accept(visitor);
  }

}
