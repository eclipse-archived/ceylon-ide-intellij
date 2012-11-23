// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonThenElseExpression extends CeylonCompositeElement {

  @NotNull
  List<CeylonDisjunctionExpression> getDisjunctionExpressionList();

  @NotNull
  List<CeylonThenElseOperator> getThenElseOperatorList();

}
