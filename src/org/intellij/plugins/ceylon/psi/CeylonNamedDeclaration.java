package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.Nullable;

public interface CeylonNamedDeclaration extends CeylonCompositeElement {

    @Nullable
    CeylonTypeNameDeclaration getTypeNameDeclaration();
}
