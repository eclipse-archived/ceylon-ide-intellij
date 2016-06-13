package org.intellij.plugins.ceylon.ide.structureView;

import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;

import javax.swing.*;

class CeylonFunctionTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.AnyMethodPsi> {

    CeylonFunctionTreeElement(CeylonPsi.AnyMethodPsi psiElement, boolean isInherited) {
        super(psiElement, isInherited);
    }

    @Override
    public Icon getIcon(boolean open) {
        return icons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
