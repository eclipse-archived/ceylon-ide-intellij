package org.intellij.plugins.ceylon.ide.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

public interface ModuleStub extends StubElement<CeylonPsi.ModuleDescriptorPsi> {

    @Nullable
    String getName();

    @Nullable
    String getVersion();

}
