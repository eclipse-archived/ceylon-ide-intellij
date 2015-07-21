package org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.impl;

import com.intellij.lang.ASTNode;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.CeylonClassImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.StubbedClassDefinitionPsi;

public class StubbedClassDefinitionPsiImpl extends CeylonClassImpl implements StubbedClassDefinitionPsi {
    public StubbedClassDefinitionPsiImpl(ASTNode node) {
        super(node);
    }
}
