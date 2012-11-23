// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonCondition extends CeylonCompositeElement {

  @Nullable
  CeylonBooleanCondition getBooleanCondition();

  @Nullable
  CeylonExistsCondition getExistsCondition();

  @Nullable
  CeylonIsCondition getIsCondition();

  @Nullable
  CeylonNonemptyCondition getNonemptyCondition();

  @Nullable
  CeylonSatisfiesCondition getSatisfiesCondition();

}
