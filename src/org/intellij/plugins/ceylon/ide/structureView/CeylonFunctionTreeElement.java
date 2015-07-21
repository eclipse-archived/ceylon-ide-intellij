package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.model.typechecker.model.Function;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
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
        Function model = getElement().getCeylonNode().getDeclarationModel();
        Icon icon = PlatformIcons.METHOD_ICON;

        if (model != null) {
            if (model.isShared()) {
                return LayeredIcon.createHorizontalIcon(icon, PlatformIcons.PUBLIC_ICON);
            } else {
                return LayeredIcon.createHorizontalIcon(icon, PlatformIcons.PRIVATE_ICON);
            }
        }

        return icon;
    }
}
