package org.intellij.plugins.ceylon.ide.structureView;

import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonFunctionTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.AnyMethodPsi> {

    protected CeylonFunctionTreeElement(CeylonPsi.AnyMethodPsi psiElement, boolean isInherited) {
        super(psiElement, isInherited);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "function " + getName() + getPresentableTypeParameters() + getPresentableParameters();
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
