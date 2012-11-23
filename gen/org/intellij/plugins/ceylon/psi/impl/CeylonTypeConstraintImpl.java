// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonTypeConstraintImpl extends CeylonCompositeElementImpl implements CeylonTypeConstraint {

  public CeylonTypeConstraintImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonAbstractedType getAbstractedType() {
    return findChildByClass(CeylonAbstractedType.class);
  }

  @Override
  @Nullable
  public CeylonCaseTypes getCaseTypes() {
    return findChildByClass(CeylonCaseTypes.class);
  }

  @Override
  @NotNull
  public CeylonCompilerAnnotations getCompilerAnnotations() {
    return findNotNullChildByClass(CeylonCompilerAnnotations.class);
  }

  @Override
  @Nullable
  public CeylonParameters getParameters() {
    return findChildByClass(CeylonParameters.class);
  }

  @Override
  @Nullable
  public CeylonSatisfiedTypes getSatisfiedTypes() {
    return findChildByClass(CeylonSatisfiedTypes.class);
  }

  @Override
  @NotNull
  public CeylonTypeNameDeclaration getTypeNameDeclaration() {
    return findNotNullChildByClass(CeylonTypeNameDeclaration.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitTypeConstraint(this);
    else super.accept(visitor);
  }

}
