package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

public class CeylonMethodTreeElement extends PsiTreeElementBase<CeylonPsi.AnyMethodPsi> {
    protected CeylonMethodTreeElement(CeylonPsi.AnyMethodPsi psiElement) {
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
        return ((Tree.AnyMethod) getElement().getCeylonNode()).getIdentifier().getText();
    }

    @Override
    public Icon getIcon(boolean open) {
        return PlatformIcons.METHOD_ICON;
    }
}
