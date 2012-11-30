// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonAliasDeclaration extends CeylonTypedDeclaration, CeylonNamedDeclaration {

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @NotNull
  CeylonTypeName getTypeName();

  @Nullable
  CeylonTypeParameters getTypeParameters();

  @Nullable
  CeylonTypeSpecifier getTypeSpecifier();

}
