// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.*;

public interface CeylonDeclaration extends CeylonCompositeElement {

  @Nullable
  CeylonAliasDeclaration getAliasDeclaration();

  @NotNull
  CeylonAnnotations getAnnotations();

  @Nullable
  CeylonClassDeclaration getClassDeclaration();

  @Nullable
  CeylonInferredAttributeDeclaration getInferredAttributeDeclaration();

  @Nullable
  CeylonInterfaceDeclaration getInterfaceDeclaration();

  @Nullable
  CeylonObjectDeclaration getObjectDeclaration();

  @Nullable
  CeylonSetterDeclaration getSetterDeclaration();

  @Nullable
  CeylonTypedMethodOrAttributeDeclaration getTypedMethodOrAttributeDeclaration();

  @Nullable
  CeylonVoidOrInferredMethodDeclaration getVoidOrInferredMethodDeclaration();

}
