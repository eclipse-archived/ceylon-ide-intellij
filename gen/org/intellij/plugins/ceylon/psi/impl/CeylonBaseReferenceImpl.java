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

public class CeylonBaseReferenceImpl extends CeylonCompositeElementImpl implements CeylonBaseReference {

  public CeylonBaseReferenceImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonMemberReference getMemberReference() {
    return findChildByClass(CeylonMemberReference.class);
  }

  @Override
  @Nullable
  public CeylonSupertypeQualifier getSupertypeQualifier() {
    return findChildByClass(CeylonSupertypeQualifier.class);
  }

  @Override
  @Nullable
  public CeylonTypeReference getTypeReference() {
    return findChildByClass(CeylonTypeReference.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitBaseReference(this);
    else super.accept(visitor);
  }

}
