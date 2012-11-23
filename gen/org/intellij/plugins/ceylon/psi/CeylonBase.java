// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonBase extends CeylonCompositeElement {

  @Nullable
  CeylonBaseReference getBaseReference();

  @Nullable
  CeylonEnumeration getEnumeration();

  @Nullable
  CeylonNonstringLiteral getNonstringLiteral();

  @Nullable
  CeylonParExpression getParExpression();

  @Nullable
  CeylonSelfReference getSelfReference();

  @Nullable
  CeylonStringExpression getStringExpression();

}
