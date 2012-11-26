// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

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
  CeylonTypeName getTypeName();

}
