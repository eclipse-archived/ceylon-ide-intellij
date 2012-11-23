// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonConjunctionExpression extends CeylonCompositeElement {

  @NotNull
  List<CeylonConjunctionOperator> getConjunctionOperatorList();

  @NotNull
  List<CeylonLogicalNegationExpression> getLogicalNegationExpressionList();

}
