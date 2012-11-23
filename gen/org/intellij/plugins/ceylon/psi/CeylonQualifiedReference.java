// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonQualifiedReference extends CeylonCompositeElement {

  @Nullable
  CeylonMemberReference getMemberReference();

  @NotNull
  CeylonMemberSelectionOperator getMemberSelectionOperator();

  @Nullable
  CeylonTypeReference getTypeReference();

}
