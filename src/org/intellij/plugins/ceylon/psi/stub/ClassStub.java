package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;

public interface ClassStub extends StubElement<CeylonClassDeclaration> {

    String getName();
}
