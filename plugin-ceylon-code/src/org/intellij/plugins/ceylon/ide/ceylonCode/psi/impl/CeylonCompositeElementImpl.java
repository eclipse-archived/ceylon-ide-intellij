package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonStubFileElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;

public class CeylonCompositeElementImpl extends ASTWrapperPsiElement implements CeylonCompositeElement {

    private Node ceylonNode;

    public CeylonCompositeElementImpl(ASTNode node) {
        this(node, node.getUserData(CeylonStubFileElementType.CEYLON_NODE_KEY));
    }

    public CeylonCompositeElementImpl(ASTNode node, Node ceylonNode) {
        super(node);
        this.ceylonNode = ceylonNode;
    }

    @Override
    public Node getCeylonNode() {
        return ceylonNode;
    }

    @Override
    public String toString() {
        return getNode().getElementType().toString();
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProviders.getItemPresentation(this);
    }
}
