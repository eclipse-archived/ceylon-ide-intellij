package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * TODO
 */
public class CeylonDeclarationTreeElement extends PsiTreeElementBase<CeylonPsi.TypedDeclarationPsi> {
    public CeylonDeclarationTreeElement(CeylonPsi.TypedDeclarationPsi child) {
        super(child);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }


    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getNode().findChildByType(CeylonTypes.IDENTIFIER).getText();
    }

    @Override
    public Icon getIcon(boolean open) {
        boolean isMethod = getElement().getNode().findChildByType(CeylonTypes.PARAMETER_LIST) != null;
        return isMethod ? PlatformIcons.METHOD_ICON : PlatformIcons.FIELD_ICON;
    }
}
