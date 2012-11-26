package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface CeylonClass extends PsiNameIdentifierOwner, CeylonClassDeclaration {

    boolean isInterface();

    @Nullable
    CeylonAdaptedTypes getAdaptedTypes();

    CeylonInterfaceBody getInterfaceBody();

    String getQualifiedName();

    CeylonClass getParentClass();
}
