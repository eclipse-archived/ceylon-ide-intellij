// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonTypeConstraint extends CeylonCompositeElement {

  @Nullable
  CeylonAbstractedType getAbstractedType();

  @Nullable
  CeylonCaseTypes getCaseTypes();

  @NotNull
  CeylonCompilerAnnotations getCompilerAnnotations();

  @Nullable
  CeylonParameters getParameters();

  @Nullable
  CeylonSatisfiedTypes getSatisfiedTypes();

  @NotNull
  CeylonTypeNameDeclaration getTypeNameDeclaration();

}
