package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.intellij.plugins.ceylon.ide.psi.CeylonCompositeElement;

public class CeylonCompositeElementImpl extends ASTWrapperPsiElement implements CeylonCompositeElement {

    private Node ceylonNode;

    public CeylonCompositeElementImpl(ASTNode node) {
        super(node);
        this.ceylonNode = node.getUserData(org.intellij.plugins.ceylon.ide.psi.parserConstants_.get_().getCeylonNodeKey());
    }

    @Override
    public Node getCeylonNode() {
        return ceylonNode;
    }

    @Override
    public String toString() {
        return getNode().toString();
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProviders.getItemPresentation(this);
    }
}
