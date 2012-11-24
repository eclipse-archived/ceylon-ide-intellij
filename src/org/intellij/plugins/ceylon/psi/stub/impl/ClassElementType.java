package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.impl.CeylonClassImpl;
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
        return new CeylonClassImpl(stub, this);
    }

    @Override
    public ClassStub createStub(@NotNull CeylonClass psi, StubElement parentStub) {
        byte flags = 0;

        if (psi.isInterface()) {
            flags |= ClassStubImpl.INTERFACE;
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
        dataStream.writeByte(stub.isInterface() ? 1 : 0); // TODO pack flags
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
