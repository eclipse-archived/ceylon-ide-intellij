// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonNamedArgumentDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonInferredGetterArgument getInferredGetterArgument();

  @Nullable
  CeylonObjectArgument getObjectArgument();

  @Nullable
  CeylonTypedMethodOrGetterArgument getTypedMethodOrGetterArgument();

  @Nullable
  CeylonVoidOrInferredMethodArgument getVoidOrInferredMethodArgument();

}
