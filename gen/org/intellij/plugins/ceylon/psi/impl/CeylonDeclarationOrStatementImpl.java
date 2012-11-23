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

public class CeylonDeclarationOrStatementImpl extends CeylonCompositeElementImpl implements CeylonDeclarationOrStatement {

  public CeylonDeclarationOrStatementImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonAnnotation> getAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonAnnotation.class);
  }

  @Override
  @Nullable
  public CeylonAssertion getAssertion() {
    return findChildByClass(CeylonAssertion.class);
  }

  @Override
  @NotNull
  public CeylonCompilerAnnotations getCompilerAnnotations() {
    return findNotNullChildByClass(CeylonCompilerAnnotations.class);
  }

  @Override
  @Nullable
  public CeylonDeclaration getDeclaration() {
    return findChildByClass(CeylonDeclaration.class);
  }

  @Override
  @Nullable
  public CeylonStatement getStatement() {
    return findChildByClass(CeylonStatement.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitDeclarationOrStatement(this);
    else super.accept(visitor);
  }

}
