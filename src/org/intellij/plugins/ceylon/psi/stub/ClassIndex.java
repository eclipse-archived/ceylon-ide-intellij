package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.jetbrains.annotations.NotNull;

public class ClassIndex extends StringStubIndexExtension<CeylonClass> {
    public static final StubIndexKey<String, CeylonClass> KEY = StubIndexKey.createIndexKey("ceylon-class.index");
    private static final ClassIndex MY_INSTANCE = new ClassIndex();

    public static ClassIndex getInstance() {
        return MY_INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CeylonClass> getKey() {
        return KEY;
    }
}
