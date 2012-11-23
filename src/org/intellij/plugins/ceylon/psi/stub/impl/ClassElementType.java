package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.impl.CeylonClassDeclarationImpl;
import org.intellij.plugins.ceylon.psi.impl.CeylonInterfaceDeclarationImpl;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClassElementType extends IStubElementType<ClassStub, CeylonNamedDeclaration> {

    public ClassElementType(@NotNull @NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }

    @Override
    public CeylonNamedDeclaration createPsi(@NotNull ClassStub stub) {
        if (stub.getStubType() == CeylonTypes.CLASS_DECLARATION) {
            return new CeylonClassDeclarationImpl(stub, this);
        } else if (stub.getStubType() == CeylonTypes.INTERFACE_DECLARATION) {
            return new CeylonInterfaceDeclarationImpl(stub, this);
        }

        throw new UnsupportedOperationException("Can't create a PsiElement for element type " + stub.getStubType());
    }

    @Override
    public ClassStub createStub(@NotNull CeylonNamedDeclaration psi, StubElement parentStub) {
        String name = null;
        byte flags = 0;

        if (psi instanceof CeylonClassDeclaration) {
            CeylonTypeNameDeclaration decl = psi.getTypeNameDeclaration();
            name = (decl == null) ? null : decl.getText();
        } else if (psi instanceof CeylonInterfaceDeclaration) {
            CeylonTypeNameDeclaration decl = psi.getTypeNameDeclaration();
            name = (decl == null) ? null : decl.getText();
            flags |= ClassStubImpl.INTERFACE;
        }

        return new ClassStubImpl((IStubElementType) psi.getNode().getElementType(), parentStub, name, null, flags);
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
        String name = stub.getName();

        if (name != null) {
            sink.occurrence(ClassIndex.KEY, name);
        }
    }
}
