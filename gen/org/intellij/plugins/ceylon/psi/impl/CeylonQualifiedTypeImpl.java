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

public class CeylonQualifiedTypeImpl extends CeylonCompositeElementImpl implements CeylonQualifiedType {

  public CeylonQualifiedTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonSupertypeQualifier getSupertypeQualifier() {
    return findChildByClass(CeylonSupertypeQualifier.class);
  }

  @Override
  @NotNull
  public List<CeylonTypeNameWithArguments> getTypeNameWithArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonTypeNameWithArguments.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitQualifiedType(this);
    else super.accept(visitor);
  }

}
