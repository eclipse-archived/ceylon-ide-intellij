package org.intellij.plugins.ceylon.psi;

import org.jetbrains.annotations.Nullable;

/**
 * Marker interface for declarations which can have typed parameters (classes, interfaces etc.)
 */
public interface CeylonTypedDeclaration extends CeylonCompositeElement {

    @Nullable
    CeylonTypeParameters getTypeParameters();
}
