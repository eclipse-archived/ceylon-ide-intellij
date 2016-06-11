package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.structureView.CeylonFileTreeElement.getTreeElementForDeclaration;

/**
 * A structure node which represents a CeylonClass (class or interface definition/declaration).
 */
class CeylonClassTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.ClassOrInterfacePsi> {
    private CeylonPsi.ClassOrInterfacePsi myClass;

    CeylonClassTreeElement(CeylonPsi.ClassOrInterfacePsi element, boolean isInherited) {
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
            Tree.ClassDefinition definition =
                    (CustomTree.ClassDefinition) ceylonNode;
            bodyChildren = definition.getClassBody().getStatements();
        } else if (ceylonNode instanceof Tree.InterfaceDefinition) {
            Tree.InterfaceDefinition definition =
                    (Tree.InterfaceDefinition) ceylonNode;
            bodyChildren = definition.getInterfaceBody().getStatements();
        } else {
            bodyChildren = Collections.emptyList();
        }

        for (Node node : bodyChildren) {
            if (node instanceof Tree.Declaration) {
                StructureViewTreeElement child =
                        getTreeElementForDeclaration(
                                (CeylonFile) myClass.getContainingFile(),
                                (Tree.Declaration) node, false);
                if (child != null) {
                    elements.add(child);
                }
            } else if (node instanceof Tree.SpecifierStatement) {
                if (((Tree.SpecifierStatement) node).getRefinement()) {
                    CeylonPsi.SpecifierStatementPsi spec =
                            PsiTreeUtil.getParentOfType(
                                    myClass.getContainingFile()
                                            .findElementAt(node.getStartIndex()),
                                    CeylonPsi.SpecifierStatementPsi.class);
                    elements.add(new CeylonSpecifierTreeElement(spec));
                }
            }
        }

        return elements;
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
