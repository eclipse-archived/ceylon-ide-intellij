package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.psi.CeylonNamedDeclaration;
import org.jetbrains.annotations.Nullable;

public interface ClassStub extends StubElement<CeylonNamedDeclaration> {

    @Nullable
    String getName();

    @Nullable
    String getQualifiedName();

    boolean isInterface();
}
