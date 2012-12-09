package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.*;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.CeylonModuleDescriptor;
import org.intellij.plugins.ceylon.psi.impl.CeylonModuleDescriptorImpl;
import org.intellij.plugins.ceylon.psi.stub.ModuleIndex;
import org.intellij.plugins.ceylon.psi.stub.ModuleStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleElementType extends IStubElementType<ModuleStub, CeylonModuleDescriptor> {

    public ModuleElementType(@NotNull @NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }

    @Override
    public CeylonModuleDescriptor createPsi(@NotNull ModuleStub stub) {
        return new CeylonModuleDescriptorImpl(stub, this);
    }

    @Override
    public ModuleStub createStub(@NotNull CeylonModuleDescriptor psi, StubElement parentStub) {
        return new ModuleStubImpl((IStubElementType) psi.getNode().getElementType(), parentStub, psi.getPackagePath().getText(), psi.getModuleVersion().getText());
    }

    @Override
    public String getExternalId() {
        return "ceylon.module";
    }

    @Override
    public void serialize(ModuleStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeName(stub.getVersion());
    }

    @Override
    public ModuleStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ModuleStubImpl(parentStub, dataStream.readName(), dataStream.readName());
    }

    @Override
    public void indexStub(ModuleStub stub, IndexSink sink) {
        String name = stub.getName();

        if (name != null) {
            sink.occurrence(ModuleIndex.KEY, name);
        }
    }
}
