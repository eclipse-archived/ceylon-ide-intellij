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

public class CeylonTypedMethodOrAttributeDeclarationImpl extends CeylonCompositeElementImpl implements CeylonTypedMethodOrAttributeDeclaration {

  public CeylonTypedMethodOrAttributeDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonBlock getBlock() {
    return findChildByClass(CeylonBlock.class);
  }

  @Override
  @Nullable
  public CeylonInitializer getInitializer() {
    return findChildByClass(CeylonInitializer.class);
  }

  @Override
  @NotNull
  public CeylonMemberNameDeclaration getMemberNameDeclaration() {
    return findNotNullChildByClass(CeylonMemberNameDeclaration.class);
  }

  @Override
  @NotNull
  public List<CeylonParameters> getParametersList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonParameters.class);
  }

  @Override
  @Nullable
  public CeylonSpecifier getSpecifier() {
    return findChildByClass(CeylonSpecifier.class);
  }

  @Override
  @NotNull
  public CeylonType getType() {
    return findNotNullChildByClass(CeylonType.class);
  }

  @Override
  @Nullable
  public CeylonTypeConstraints getTypeConstraints() {
    return findChildByClass(CeylonTypeConstraints.class);
  }

  @Override
  @Nullable
  public CeylonTypeParameters getTypeParameters() {
    return findChildByClass(CeylonTypeParameters.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypedMethodOrAttributeDeclaration(this);
    else super.accept(visitor);
  }

}
