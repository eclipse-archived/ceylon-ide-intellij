// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonAdditiveExpression extends CeylonCompositeElement {

  @NotNull
  List<CeylonAdditiveOperator> getAdditiveOperatorList();

  @NotNull
  List<CeylonMultiplicativeExpression> getMultiplicativeExpressionList();

}
