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

public class CeylonExtendedTypeImpl extends CeylonCompositeElementImpl implements CeylonExtendedType {

  public CeylonExtendedTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitExtendedType(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonPositionalArguments getPositionalArguments() {
    return findNotNullChildByClass(CeylonPositionalArguments.class);
  }

  @Override
  @Nullable
  public CeylonQualifiedType getQualifiedType() {
    return findChildByClass(CeylonQualifiedType.class);
  }

  @Override
  @Nullable
  public CeylonTypeReference getTypeReference() {
    return findChildByClass(CeylonTypeReference.class);
  }

}
