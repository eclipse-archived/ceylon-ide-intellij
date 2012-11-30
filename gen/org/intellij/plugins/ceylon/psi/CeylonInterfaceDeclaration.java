// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import com.intellij.psi.StubBasedPsiElement;

public interface CeylonInterfaceDeclaration extends CeylonTypedDeclaration, StubBasedPsiElement<ClassStub> {

  @Nullable
  CeylonAdaptedTypes getAdaptedTypes();

  @Nullable
  CeylonBlock getBlock();

  @Nullable
  CeylonCaseTypes getCaseTypes();

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
