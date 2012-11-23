// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import com.intellij.psi.StubBasedPsiElement;

public interface CeylonInterfaceDeclaration extends CeylonNamedDeclaration, StubBasedPsiElement<ClassStub> {

  @Nullable
  CeylonAdaptedTypes getAdaptedTypes();

  @Nullable
  CeylonCaseTypes getCaseTypes();

  @Nullable
  CeylonInterfaceBody getInterfaceBody();

  @Nullable
  CeylonSatisfiedTypes getSatisfiedTypes();

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @Nullable
  CeylonTypeNameDeclaration getTypeNameDeclaration();

  @Nullable
  CeylonTypeParameters getTypeParameters();

  @Nullable
  CeylonTypeSpecifier getTypeSpecifier();

}
