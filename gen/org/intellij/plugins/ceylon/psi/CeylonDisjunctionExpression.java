// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonDisjunctionExpression extends CeylonCompositeElement {

  @NotNull
  List<CeylonConjunctionExpression> getConjunctionExpressionList();

  @NotNull
  List<CeylonDisjunctionOperator> getDisjunctionOperatorList();

}
