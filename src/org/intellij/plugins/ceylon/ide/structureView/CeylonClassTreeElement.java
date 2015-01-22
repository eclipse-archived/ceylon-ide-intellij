package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonTypes;
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
public class CeylonClassTreeElement extends PsiTreeElementBase<CeylonClass> {
    private CeylonClass myClass;

    public CeylonClassTreeElement(CeylonClass element) {
        super(element);
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
                StructureViewTreeElement child = getTreeElementForDeclaration((CeylonFile) myClass.getContainingFile(), (Tree.Declaration) node);
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
        Tree.Declaration ceylonNode = myClass.getCeylonNode();

        if (ceylonNode != null) {
            return ceylonNode.getIdentifier().getText();
        }

        ASTNode identifier = myClass.getNode().findChildByType(CeylonTypes.IDENTIFIER);
        return (identifier == null) ? null : identifier.getText();
    }

    @Override
    public Icon getIcon(boolean open) {
        return myClass.isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
    }
}
