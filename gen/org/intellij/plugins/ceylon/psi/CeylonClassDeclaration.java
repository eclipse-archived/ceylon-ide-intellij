// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonClassDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonCaseTypes getCaseTypes();

  @Nullable
  CeylonClassBody getClassBody();

  @Nullable
  CeylonExtendedType getExtendedType();

  @Nullable
  CeylonParameters getParameters();

  @Nullable
  CeylonSatisfiedTypes getSatisfiedTypes();

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @NotNull
  CeylonTypeNameDeclaration getTypeNameDeclaration();

  @Nullable
  CeylonTypeParameters getTypeParameters();

  @Nullable
  CeylonTypeSpecifier getTypeSpecifier();

}
