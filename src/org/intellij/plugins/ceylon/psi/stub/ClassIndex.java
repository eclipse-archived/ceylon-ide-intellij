package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.intellij.plugins.ceylon.psi.CeylonNamedDeclaration;
import org.jetbrains.annotations.NotNull;

public class ClassIndex extends StringStubIndexExtension<CeylonNamedDeclaration> {
    public static final StubIndexKey<String, CeylonNamedDeclaration> KEY = StubIndexKey.createIndexKey("ceylon-class.index");
    private static final ClassIndex MY_INSTANCE = new ClassIndex();

    public static ClassIndex getInstance() {
        return MY_INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CeylonNamedDeclaration> getKey() {
        return KEY;
    }
}
