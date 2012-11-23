// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

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
