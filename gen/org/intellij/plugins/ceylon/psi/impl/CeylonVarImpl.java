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

public class CeylonVarImpl extends CeylonCompositeElementImpl implements CeylonVar {

  public CeylonVarImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitVar(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonMemberName getMemberName() {
    return findNotNullChildByClass(CeylonMemberName.class);
  }

  @Override
  @NotNull
  public List<CeylonParameters> getParametersList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonParameters.class);
  }

  @Override
  @Nullable
  public CeylonType getType() {
    return findChildByClass(CeylonType.class);
  }

}
