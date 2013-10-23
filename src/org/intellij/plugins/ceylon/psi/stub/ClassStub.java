package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.jetbrains.annotations.Nullable;

public interface ClassStub extends StubElement<CeylonClass> {

    @Nullable
    String getName();

    @Nullable
    String getQualifiedName();

}
