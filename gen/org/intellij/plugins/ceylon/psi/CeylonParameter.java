// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonParameter extends CeylonCompositeElement {

  @NotNull
  CeylonMemberName getMemberName();

  @NotNull
  CeylonParameterType getParameterType();

  @NotNull
  List<CeylonParameters> getParametersList();

  @Nullable
  CeylonSpecifier getSpecifier();

  @Nullable
  CeylonValueParameter getValueParameter();

}
