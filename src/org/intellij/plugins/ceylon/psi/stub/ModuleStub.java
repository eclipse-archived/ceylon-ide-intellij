package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.psi.CeylonModuleDescriptor;
import org.jetbrains.annotations.Nullable;

public interface ModuleStub extends StubElement<CeylonModuleDescriptor> {

    @Nullable
    String getName();

    @Nullable
    String getVersion();

}
