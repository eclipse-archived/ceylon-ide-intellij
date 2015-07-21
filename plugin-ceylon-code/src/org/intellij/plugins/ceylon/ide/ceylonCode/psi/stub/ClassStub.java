package org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.jetbrains.annotations.Nullable;

public interface ClassStub extends StubElement<CeylonClass> {

    @Nullable
    String getName();

    @Nullable
    String getQualifiedName();

    byte getFlags();

    boolean isInterface();
}
