package org.intellij.plugins.ceylon.ide.psi.stub;

import com.intellij.psi.StubBasedPsiElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonClass;

public interface StubbedClassDefinitionPsi extends CeylonClass, StubBasedPsiElement<ClassStub> {
}
