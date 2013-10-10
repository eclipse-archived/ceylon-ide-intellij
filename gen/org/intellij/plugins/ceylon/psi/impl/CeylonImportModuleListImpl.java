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

public class CeylonImportModuleListImpl extends CeylonCompositeElementImpl implements CeylonImportModuleList {

  public CeylonImportModuleListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitImportModuleList(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CeylonAnnotations> getAnnotationsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonAnnotations.class);
  }

  @Override
  @NotNull
  public List<CeylonCompilerAnnotations> getCompilerAnnotationsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonCompilerAnnotations.class);
  }

  @Override
  @NotNull
  public List<CeylonImportModule> getImportModuleList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CeylonImportModule.class);
  }

}
