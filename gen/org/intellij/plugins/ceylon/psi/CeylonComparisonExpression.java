// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonComparisonExpression extends CeylonCompositeElement {

  @Nullable
  CeylonComparableType getComparableType();

  @Nullable
  CeylonComparisonOperator getComparisonOperator();

  @NotNull
  List<CeylonExistenceEmptinessExpression> getExistenceEmptinessExpressionList();

  @Nullable
  CeylonType getType();

  @Nullable
  CeylonTypeOperator getTypeOperator();

}
