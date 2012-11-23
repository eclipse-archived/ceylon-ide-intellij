// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonVoidOrInferredMethodDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonBlock getBlock();

  @NotNull
  CeylonMemberNameDeclaration getMemberNameDeclaration();

  @NotNull
  List<CeylonParameters> getParametersList();

  @Nullable
  CeylonSpecifier getSpecifier();

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @Nullable
  CeylonTypeParameters getTypeParameters();

}
