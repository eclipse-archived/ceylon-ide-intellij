// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;

public class CeylonCompilerAnnotationsImpl extends CeylonCompositeElementImpl implements CeylonCompilerAnnotations {

  public CeylonCompilerAnnotationsImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<CeylonCompilerAnnotation> getCompilerAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCompilerAnnotation.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitCompilerAnnotations(this);
    else super.accept(visitor);
  }

}
