package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.intellij.plugins.ceylon.psi.CeylonModuleDescriptor;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.stub.ModuleStub;

public class ModuleStubImpl extends StubBase<CeylonModuleDescriptor> implements ModuleStub {

    private String name;
    private String version;


    protected ModuleStubImpl(IStubElementType elementType, StubElement parent, String name, String version) {
        super(parent, elementType);
        this.name = name;
        this.version = version;
    }

    public ModuleStubImpl(StubElement parent, StringRef name, StringRef version) {
        super(parent, (IStubElementType) CeylonTypes.MODULE_DESCRIPTOR);

        if (name != null) {
            this.name = name.getString();
        }
        if (version != null) {
            this.version = version.getString();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }
}
