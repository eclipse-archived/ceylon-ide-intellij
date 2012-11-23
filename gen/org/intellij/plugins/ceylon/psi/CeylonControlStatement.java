// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonControlStatement extends CeylonCompositeElement {

  @Nullable
  CeylonForElse getForElse();

  @Nullable
  CeylonIfElse getIfElse();

  @Nullable
  CeylonSwitchCaseElse getSwitchCaseElse();

  @Nullable
  CeylonTryCatchFinally getTryCatchFinally();

  @Nullable
  CeylonWhileLoop getWhileLoop();

}
