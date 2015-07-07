package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.icons.AllIcons;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.structureView.CeylonFileTreeElement.getTreeElementForDeclaration;

public class CeylonObjectTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.ObjectDefinitionPsi> {

    protected CeylonObjectTreeElement(CeylonPsi.ObjectDefinitionPsi psiElement, boolean isInherited) {
        super(psiElement, isInherited);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        List<StructureViewTreeElement> children = new ArrayList<>();

        for (Tree.Statement statement : getElement().getCeylonNode().getClassBody().getStatements()) {
            if (statement instanceof Tree.Declaration) {
                StructureViewTreeElement node = getTreeElementForDeclaration((CeylonFile) getElement().getContainingFile(), (Tree.Declaration) statement, false);
                if (node != null) {
                    children.add(node);
                }
            }
        }

        return children;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "object " + getName();
    }

    @Override
    public Icon getIcon(boolean open) {
        return AllIcons.Nodes.AnonymousClass;
    }
}
