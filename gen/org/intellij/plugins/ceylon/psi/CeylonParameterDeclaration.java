// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonParameterDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonAnnotations getAnnotations();

  @NotNull
  CeylonCompilerAnnotations getCompilerAnnotations();

  @Nullable
  CeylonParameter getParameter();

  @Nullable
  CeylonParameterRef getParameterRef();

}
