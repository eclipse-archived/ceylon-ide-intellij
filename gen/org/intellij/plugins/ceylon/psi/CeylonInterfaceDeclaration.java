// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonInterfaceDeclaration extends CeylonCompositeElement {

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

  @NotNull
  CeylonTypeNameDeclaration getTypeNameDeclaration();

  @Nullable
  CeylonTypeParameters getTypeParameters();

  @Nullable
  CeylonTypeSpecifier getTypeSpecifier();

}
