package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.structureView.CeylonFileTreeElement.getTreeElementForDeclaration;

/**
 * A structure node which represents a CeylonClass (class or interface definition/declaration).
 */
public class CeylonClassTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.ClassOrInterfacePsi> {
    private CeylonPsi.ClassOrInterfacePsi myClass;

    public CeylonClassTreeElement(CeylonPsi.ClassOrInterfacePsi element, boolean isInherited) {
        super(element, isInherited);
        this.myClass = element;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        Tree.ClassOrInterface ceylonNode = myClass.getCeylonNode();

        List<StructureViewTreeElement> elements = new ArrayList<>();
        List<Tree.Statement> bodyChildren;

        if (ceylonNode instanceof CustomTree.ClassDefinition) {
            bodyChildren = ((CustomTree.ClassDefinition) ceylonNode).getClassBody().getStatements();
        } else if (ceylonNode instanceof Tree.InterfaceDefinition) {
            bodyChildren = ((Tree.InterfaceDefinition) ceylonNode).getInterfaceBody().getStatements();
        } else {
            bodyChildren = Collections.emptyList();
        }

        for (Node node : bodyChildren) {
            if (node instanceof Tree.Declaration) {
                StructureViewTreeElement child = getTreeElementForDeclaration((CeylonFile) myClass.getContainingFile(), (Tree.Declaration) node, false);
                if (child != null) {
                    elements.add(child);
                }
            }
        }

        return elements;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        String kind = myClass instanceof CeylonPsi.AnyInterfacePsi ? "interface " : "class ";

        return kind + getName() + getPresentableTypeParameters() + getPresentableParameters();
    }

    @Override
    public Icon getIcon(boolean open) {
        Declaration model = getElement().getCeylonNode().getDeclarationModel();
        Icon icon = myClass instanceof CeylonPsi.AnyInterfacePsi ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;

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
