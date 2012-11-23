package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypeNameDeclaration;
import org.intellij.plugins.ceylon.psi.impl.CeylonClassDeclarationImpl;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClassElementType extends IStubElementType<ClassStub, CeylonClassDeclaration> {

    public ClassElementType(@NotNull @NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }

    @Override
    public CeylonClassDeclaration createPsi(@NotNull ClassStub stub) {
        return new CeylonClassDeclarationImpl(stub, this);
    }

    @Override
    public ClassStub createStub(@NotNull CeylonClassDeclaration psi, StubElement parentStub) {
        CeylonTypeNameDeclaration decl = psi.getTypeNameDeclaration();
        String name = (decl == null) ? null : decl.getText();

        return new ClassStubImpl(parentStub, name);
    }

    @Override
    public String getExternalId() {
        return "ceylon.class";
    }

    @Override
    public void serialize(ClassStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @Override
    public ClassStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ClassStubImpl(parentStub, dataStream.readName());
    }

    @Override
    public void indexStub(ClassStub stub, IndexSink sink) {
        if (stub.getName() != null) {
            sink.occurrence(ClassIndex.KEY, stub.getName());
        }
    }
}
