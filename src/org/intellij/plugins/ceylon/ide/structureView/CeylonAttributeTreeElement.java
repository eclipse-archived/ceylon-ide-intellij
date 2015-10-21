package org.intellij.plugins.ceylon.ide.structureView;

import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonAttributeTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.AttributeDeclarationPsi> {

    protected CeylonAttributeTreeElement(CeylonPsi.AttributeDeclarationPsi declaration, boolean isInherited) {
        super(declaration, isInherited);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "value " + getName();
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
