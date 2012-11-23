// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonAssignmentExpression extends CeylonCompositeElement {

  @Nullable
  CeylonAssignmentExpression getAssignmentExpression();

  @Nullable
  CeylonAssignmentOperator getAssignmentOperator();

  @NotNull
  CeylonThenElseExpression getThenElseExpression();

}
