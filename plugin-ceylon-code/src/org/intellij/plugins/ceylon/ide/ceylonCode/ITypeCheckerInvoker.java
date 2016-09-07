package org.intellij.plugins.ceylon.ide.ceylonCode;

import com.intellij.openapi.extensions.ExtensionPointName;

import java.io.File;

public interface ITypeCheckerInvoker {
    ExtensionPointName<ITypeCheckerInvoker> EP_NAME
            = ExtensionPointName.create("org.intellij.plugins.ceylon.ide.typecheckerInvoker");

    File getEmbeddedCeylonRepository();
    File getEmbeddedCeylonDist();
    File getSupplementalCeylonRepository();
}
