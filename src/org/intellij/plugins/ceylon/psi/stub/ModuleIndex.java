package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.intellij.plugins.ceylon.psi.CeylonModuleDescriptor;
import org.jetbrains.annotations.NotNull;

public class ModuleIndex extends StringStubIndexExtension<CeylonModuleDescriptor> {
    public static final StubIndexKey<String, CeylonModuleDescriptor> KEY = StubIndexKey.createIndexKey("ceylon-module.index");
    private static final ModuleIndex MY_INSTANCE = new ModuleIndex();

    public static ModuleIndex getInstance() {
        return MY_INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CeylonModuleDescriptor> getKey() {
        return KEY;
    }
}
