package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.CeylonTypedMethodOrAttributeDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

public class CeylonMethodTreeElement extends PsiTreeElementBase<CeylonTypedMethodOrAttributeDeclaration> {
    protected CeylonMethodTreeElement(CeylonTypedMethodOrAttributeDeclaration psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getMemberName().getText();
    }

    @Override
    public Icon getIcon(boolean open) {
        if (getElement().getBlock() == null && getElement().getParametersList().isEmpty()) {
            return PlatformIcons.FIELD_ICON;
        }
        return PlatformIcons.METHOD_ICON;
    }
}
