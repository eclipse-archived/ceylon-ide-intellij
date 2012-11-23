// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonVoidOrInferredMethodArgumentImpl extends CeylonCompositeElementImpl implements CeylonVoidOrInferredMethodArgument {

  public CeylonVoidOrInferredMethodArgumentImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonBlock getBlock() {
    return findNotNullChildByClass(CeylonBlock.class);
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

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitVoidOrInferredMethodArgument(this);
    else super.accept(visitor);
  }

}
