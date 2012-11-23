// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonTypedMethodOrGetterArgument extends CeylonCompositeElement {

  @NotNull
  CeylonBlock getBlock();

  @NotNull
  CeylonMemberNameDeclaration getMemberNameDeclaration();

  @NotNull
  List<CeylonParameters> getParametersList();

  @NotNull
  CeylonType getType();

}
