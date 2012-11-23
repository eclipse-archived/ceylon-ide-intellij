// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonNamedArguments extends CeylonCompositeElement {

  @Nullable
  CeylonComprehension getComprehension();

  @NotNull
  List<CeylonNamedArgument> getNamedArgumentList();

  @Nullable
  CeylonSequencedArgument getSequencedArgument();

}
