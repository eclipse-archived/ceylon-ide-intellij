package org.intellij.plugins.ceylon.ide.ceylonCode;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;

public interface ITypeCheckerProvider {

    TypeChecker getTypeChecker();
}
