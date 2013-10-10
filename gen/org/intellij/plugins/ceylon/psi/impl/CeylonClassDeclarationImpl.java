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
import com.intellij.psi.stubs.IStubElementType;

public class CeylonClassDeclarationImpl extends CeylonClassImpl implements CeylonClassDeclaration {

  public CeylonClassDeclarationImpl(ASTNode node) {
    super(node);
  }

  public CeylonClassDeclarationImpl(org.intellij.plugins.ceylon.psi.stub.ClassStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CeylonVisitor) ((CeylonVisitor)visitor).visitClassDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public CeylonBlock getBlock() {
    return findChildByClass(CeylonBlock.class);
  }

  @Override
  @Nullable
  public CeylonCaseTypes getCaseTypes() {
    return findChildByClass(CeylonCaseTypes.class);
  }

  @Override
  @Nullable
  public CeylonExtendedType getExtendedType() {
    return findChildByClass(CeylonExtendedType.class);
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
  @Nullable
  public CeylonTypeConstraints getTypeConstraints() {
    return findChildByClass(CeylonTypeConstraints.class);
  }

  @Override
  @Nullable
  public CeylonTypeName getTypeName() {
    return findChildByClass(CeylonTypeName.class);
  }

  @Override
  @Nullable
  public CeylonTypeParameters getTypeParameters() {
    return findChildByClass(CeylonTypeParameters.class);
  }

  @Override
  @Nullable
  public CeylonTypeSpecifier getTypeSpecifier() {
    return findChildByClass(CeylonTypeSpecifier.class);
  }

}
