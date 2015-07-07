package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.model.typechecker.model.Value;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
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
        Value model = getElement().getCeylonNode().getDeclarationModel();
        Icon icon = PlatformIcons.VARIABLE_ICON;

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
