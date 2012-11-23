package org.intellij.plugins.ceylon.psi.stub.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;

public class ClassStubImpl extends StubBase<CeylonClassDeclaration> implements ClassStub {

    private String name;

    protected ClassStubImpl(StubElement parent, String name) {
        super(parent, (IStubElementType) CeylonTypes.CLASS_DECLARATION);
        this.name = name;
    }

    public ClassStubImpl(StubElement parent, StringRef stringRef) {
        super(parent, (IStubElementType) CeylonTypes.CLASS_DECLARATION);

        if (stringRef != null) {
            this.name = stringRef.getString();
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
