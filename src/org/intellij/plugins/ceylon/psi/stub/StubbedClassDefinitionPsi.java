package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.StubBasedPsiElement;
import org.intellij.plugins.ceylon.psi.CeylonClass;

public interface StubbedClassDefinitionPsi extends CeylonClass, StubBasedPsiElement<ClassStub> {
}
