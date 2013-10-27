package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.Nullable;

public interface CeylonClass extends PsiNameIdentifierOwner, CeylonNamedDeclaration, CeylonTypedDeclaration, StubBasedPsiElement<ClassStub> {

    @Nullable
    String getPackage();

    @Nullable
    String getQualifiedName();

    @Nullable
    CeylonClass getParentClass();

    boolean isInterface();
}
