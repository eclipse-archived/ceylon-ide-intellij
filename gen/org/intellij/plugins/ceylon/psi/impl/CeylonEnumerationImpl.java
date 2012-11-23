// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonEnumerationImpl extends CeylonCompositeElementImpl implements CeylonEnumeration {

  public CeylonEnumerationImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public CeylonComprehension getComprehension() {
    return findChildByClass(CeylonComprehension.class);
  }

  @Override
  @Nullable
  public CeylonSequencedArgument getSequencedArgument() {
    return findChildByClass(CeylonSequencedArgument.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitEnumeration(this);
    else super.accept(visitor);
  }

}
