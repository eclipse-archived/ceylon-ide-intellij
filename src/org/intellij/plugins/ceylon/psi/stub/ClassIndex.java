package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.jetbrains.annotations.NotNull;

public class ClassIndex extends StringStubIndexExtension<CeylonClassDeclaration> {
    public static final StubIndexKey<String, CeylonClassDeclaration> KEY = StubIndexKey.createIndexKey("ceylon-class.index");
    private static final ClassIndex MY_INSTANCE = new ClassIndex();

    public static ClassIndex getInstance() {
        return MY_INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CeylonClassDeclaration> getKey() {
        return KEY;
    }
}
