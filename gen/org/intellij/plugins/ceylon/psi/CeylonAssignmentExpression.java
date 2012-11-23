// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonAssignmentExpression extends CeylonCompositeElement {

  @Nullable
  CeylonAssignmentExpression getAssignmentExpression();

  @Nullable
  CeylonAssignmentOperator getAssignmentOperator();

  @NotNull
  CeylonThenElseExpression getThenElseExpression();

}
