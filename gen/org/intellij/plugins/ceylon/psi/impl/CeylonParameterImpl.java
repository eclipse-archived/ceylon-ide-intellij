// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonParameterImpl extends CeylonCompositeElementImpl implements CeylonParameter {

  public CeylonParameterImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public CeylonMemberName getMemberName() {
    return findNotNullChildByClass(CeylonMemberName.class);
  }

  @Override
  @NotNull
  public CeylonParameterType getParameterType() {
    return findNotNullChildByClass(CeylonParameterType.class);
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
  @Nullable
  public CeylonValueParameter getValueParameter() {
    return findChildByClass(CeylonValueParameter.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitParameter(this);
    else super.accept(visitor);
  }

}
