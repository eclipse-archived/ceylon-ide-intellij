package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.Nullable;

public class ClassStubImpl extends StubBase<CeylonClass> implements ClassStub {

    private String name;
    private String qualifiedName;
    private byte flags;

    static final int INTERFACE = 0x01;
    static final int OBJECT = 0x02;

    protected ClassStubImpl(IStubElementType elementType, StubElement parent, String name, String qualifiedName, byte flags) {
        super(parent, elementType);
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.flags = flags;
    }

    public ClassStubImpl(StubElement parent, StringRef name, StringRef qualifiedName, byte flags) {
        super(parent, (IStubElementType) ((flags & INTERFACE) == 0 ? CeylonTypes.CLASS_DEFINITION : CeylonTypes.INTERFACE_DEFINITION));

        if (name != null) {
            this.name = name.getString();
        }
        if (qualifiedName != null) {
            this.qualifiedName = qualifiedName.getString();
        }
        this.flags = flags;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public byte getFlags() {
        return flags;
    }

    @Override
    public boolean isInterface() {
        return (flags & INTERFACE) != 0;
    }
}
