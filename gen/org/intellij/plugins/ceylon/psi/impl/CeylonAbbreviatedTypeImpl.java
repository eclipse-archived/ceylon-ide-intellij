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

public class CeylonAbbreviatedTypeImpl extends CeylonCompositeElementImpl implements CeylonAbbreviatedType {

  public CeylonAbbreviatedTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonQualifiedType getQualifiedType() {
    return findNotNullChildByClass(CeylonQualifiedType.class);
  }

  @Override
  @NotNull
  public List<CeylonType> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitAbbreviatedType(this);
    else super.accept(visitor);
  }

}
