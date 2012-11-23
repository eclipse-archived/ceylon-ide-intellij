// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonDefaultExpression extends CeylonCompositeElement {

  @NotNull
  List<CeylonDefaultOperator> getDefaultOperatorList();

  @NotNull
  List<CeylonNegationComplementExpression> getNegationComplementExpressionList();

}
