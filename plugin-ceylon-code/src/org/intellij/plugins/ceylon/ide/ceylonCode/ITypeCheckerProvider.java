package org.intellij.plugins.ceylon.ide.ceylonCode;

import com.intellij.openapi.module.Module;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.jetbrains.annotations.Nullable;

public interface ITypeCheckerProvider {

    TypeChecker getTypeChecker();

    // TODO move somewhere else
    void addFacetToModule(Module module, @Nullable String jdkProvider);
}
