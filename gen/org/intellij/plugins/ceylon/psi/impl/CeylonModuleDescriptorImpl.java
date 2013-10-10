// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import org.intellij.plugins.ceylon.psi.stub.ModuleStub;
import org.intellij.plugins.ceylon.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class CeylonModuleDescriptorImpl extends StubBasedPsiElementBase<ModuleStub> implements CeylonModuleDescriptor {

  public CeylonModuleDescriptorImpl(ASTNode node) {
    super(node);
  }

  public CeylonModuleDescriptorImpl(ModuleStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitModuleDescriptor(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CeylonImportModuleList getImportModuleList() {
    return findNotNullChildByClass(CeylonImportModuleList.class);
  }

  @Override
  @NotNull
  public CeylonModuleVersion getModuleVersion() {
    return findNotNullChildByClass(CeylonModuleVersion.class);
  }

  @Override
  @NotNull
  public CeylonPackagePath getPackagePath() {
    return findNotNullChildByClass(CeylonPackagePath.class);
  }

}
