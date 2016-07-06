package org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.ClassIndex;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.CeylonClassImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClassElementType extends IStubElementType<ClassStub, CeylonClass> {

    public ClassElementType(@NotNull @NonNls String debugName) {
        super(debugName, ceylonLanguage_.get_());
    }

    @Override
    public CeylonClass createPsi(@NotNull ClassStub stub) {
        return new CeylonClassImpl(stub, this);
    }

    @Override
    public ClassStub createStub(@NotNull CeylonClass psi, StubElement parentStub) {
        byte flags = (byte) (psi.getNode().getElementType() == CeylonTypes.INTERFACE_DEFINITION ? ClassStubImpl.INTERFACE : 0);

        return new ClassStubImpl((IStubElementType) psi.getNode().getElementType(), parentStub, psi.getName(), psi.getQualifiedName(), flags);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        // Workaround to avoid duplicate stubs for nested identical ClassDefinitions
        return node.findChildByType(CeylonTypes.IDENTIFIER) != null;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "ceylon.class." + toString();
    }

    @Override
    public void serialize(@NotNull ClassStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeName(stub.getQualifiedName());
        dataStream.write(stub.getFlags());
    }

    @NotNull
    @Override
    public ClassStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ClassStubImpl(parentStub, dataStream.readName(), dataStream.readName(), dataStream.readByte());
    }

    @Override
    public void indexStub(@NotNull ClassStub stub, @NotNull IndexSink sink) {
        String name = stub.getQualifiedName();

        if (name != null) {
            sink.occurrence(ClassIndex.KEY, name);
        }
        // TODO short name index
    }
}
