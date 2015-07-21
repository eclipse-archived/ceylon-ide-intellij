package org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

public interface ModuleStub extends StubElement<CeylonPsi.ModuleDescriptorPsi> {

    @Nullable
    String getName();

    @Nullable
    String getVersion();

}
