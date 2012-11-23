// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonDeclarationImpl extends CeylonCompositeElementImpl implements CeylonDeclaration {

  public CeylonDeclarationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonAliasDeclaration getAliasDeclaration() {
    return findChildByClass(CeylonAliasDeclaration.class);
  }

  @Override
  @NotNull
  public CeylonAnnotations getAnnotations() {
    return findNotNullChildByClass(CeylonAnnotations.class);
  }

  @Override
  @Nullable
  public CeylonClassDeclaration getClassDeclaration() {
    return findChildByClass(CeylonClassDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonInferredAttributeDeclaration getInferredAttributeDeclaration() {
    return findChildByClass(CeylonInferredAttributeDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonInterfaceDeclaration getInterfaceDeclaration() {
    return findChildByClass(CeylonInterfaceDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonObjectDeclaration getObjectDeclaration() {
    return findChildByClass(CeylonObjectDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonSetterDeclaration getSetterDeclaration() {
    return findChildByClass(CeylonSetterDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonTypedMethodOrAttributeDeclaration getTypedMethodOrAttributeDeclaration() {
    return findChildByClass(CeylonTypedMethodOrAttributeDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonVoidOrInferredMethodDeclaration getVoidOrInferredMethodDeclaration() {
    return findChildByClass(CeylonVoidOrInferredMethodDeclaration.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitDeclaration(this);
    else super.accept(visitor);
  }

}
