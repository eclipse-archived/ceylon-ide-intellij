// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonTypedMethodOrAttributeDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonBlock getBlock();

  @Nullable
  CeylonInitializer getInitializer();

  @NotNull
  CeylonMemberName getMemberName();

  @NotNull
  List<CeylonParameters> getParametersList();

  @Nullable
  CeylonSpecifier getSpecifier();

  @NotNull
  CeylonType getType();

  @Nullable
  CeylonTypeConstraints getTypeConstraints();

  @Nullable
  CeylonTypeParameters getTypeParameters();

}
