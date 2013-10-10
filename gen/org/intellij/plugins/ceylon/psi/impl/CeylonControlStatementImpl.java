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

public class CeylonControlStatementImpl extends CeylonCompositeElementImpl implements CeylonControlStatement {

  public CeylonControlStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitControlStatement(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonForElse getForElse() {
    return findChildByClass(CeylonForElse.class);
  }

  @Override
  @Nullable
  public CeylonIfElse getIfElse() {
    return findChildByClass(CeylonIfElse.class);
  }

  @Override
  @Nullable
  public CeylonSwitchCaseElse getSwitchCaseElse() {
    return findChildByClass(CeylonSwitchCaseElse.class);
  }

  @Override
  @Nullable
  public CeylonTryCatchFinally getTryCatchFinally() {
    return findChildByClass(CeylonTryCatchFinally.class);
  }

  @Override
  @Nullable
  public CeylonWhileLoop getWhileLoop() {
    return findChildByClass(CeylonWhileLoop.class);
  }

}
