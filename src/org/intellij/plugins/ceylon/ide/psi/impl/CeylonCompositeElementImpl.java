package org.eclipse.ceylon.ide.intellij.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.ide.intellij.psi.CeylonCompositeElement;

public class CeylonCompositeElementImpl extends ASTWrapperPsiElement implements CeylonCompositeElement {

    private Node ceylonNode;

    public CeylonCompositeElementImpl(ASTNode node) {
        super(node);
        this.ceylonNode = node.getUserData(org.eclipse.ceylon.ide.intellij.psi.parserConstants_.get_().getCeylonNodeKey());
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
