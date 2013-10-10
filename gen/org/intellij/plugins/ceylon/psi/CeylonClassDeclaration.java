// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;

public interface CeylonClassDeclaration extends CeylonClass, StubBasedPsiElement<ClassStub> {

  @Nullable
  CeylonBlock getBlock();

  @Nullable
  CeylonCaseTypes getCaseTypes();

  @Nullable
  CeylonExtendedType getExtendedType();

  @Nullable
  CeylonParameters getParameters();

  @Nullable
  CeylonSatisfiedTypes getSatisfiedTypes();

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @Nullable
  CeylonTypeName getTypeName();

  @Nullable
  CeylonTypeParameters getTypeParameters();

  @Nullable
  CeylonTypeSpecifier getTypeSpecifier();

}
