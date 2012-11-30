package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.impl.CeylonClassDeclarationImpl;
import org.intellij.plugins.ceylon.psi.impl.CeylonInterfaceDeclarationImpl;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClassElementType extends IStubElementType<ClassStub, CeylonClass> {

    public ClassElementType(@NotNull @NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }

    @Override
    public CeylonClass createPsi(@NotNull ClassStub stub) {
        if (stub.isInterface()) {
            return new CeylonInterfaceDeclarationImpl(stub, this);
        }
        return new CeylonClassDeclarationImpl(stub, this);
    }

    @Override
    public ClassStub createStub(@NotNull CeylonClass psi, StubElement parentStub) {
        byte flags = 0;

        if (psi.isInterface()) {
            flags |= ClassStubImpl.INTERFACE;
        } else if (psi.isObject()) {
            flags |= ClassStubImpl.OBJECT;
        }

        return new ClassStubImpl((IStubElementType) psi.getNode().getElementType(), parentStub, psi.getName(), psi.getQualifiedName(), flags);
    }

    @Override
    public String getExternalId() {
        return "ceylon.class";
    }

    @Override
    public void serialize(ClassStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeName(stub.getQualifiedName());

        byte flags = 0;
        if (stub.isInterface()) {
            flags |= ClassStubImpl.INTERFACE;
        } else if (stub.isObject()) {
            flags |= ClassStubImpl.OBJECT;
        }
        dataStream.writeByte(flags);
    }

    @Override
    public ClassStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ClassStubImpl(parentStub, dataStream.readName(), dataStream.readName(), dataStream.readByte());
    }

    @Override
    public void indexStub(ClassStub stub, IndexSink sink) {
        String name = stub.getQualifiedName();

        if (name != null) {
            sink.occurrence(ClassIndex.KEY, name);
        }
        // TODO short name index
    }
}
