package org.intellij.plugins.ceylon.ide.ceylonCode;

import com.intellij.openapi.extensions.ExtensionPointName;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;

public interface ITypeCheckerInvoker {
    ExtensionPointName<ITypeCheckerInvoker> EP_NAME
            = ExtensionPointName.create("org.intellij.plugins.ceylon.ide.typecheckerInvoker");

    PhasedUnit typecheck(CeylonFile ceylonFile);
}
