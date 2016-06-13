package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.structureView.CeylonFileTreeElement.getTreeElementForDeclaration;

class CeylonObjectTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.ObjectDefinitionPsi> {

    CeylonObjectTreeElement(CeylonPsi.ObjectDefinitionPsi psiElement, boolean isInherited) {
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
            } else if (statement instanceof Tree.SpecifierStatement) {
                CeylonPsi.SpecifierStatementPsi spec = PsiTreeUtil.getParentOfType(
                        getElement().getContainingFile().findElementAt(statement.getStartIndex()),
                        CeylonPsi.SpecifierStatementPsi.class);
                children.add(new CeylonSpecifierTreeElement(spec));
            }
        }

        return children;
    }

    @Override
    public Icon getIcon(boolean open) {
        return icons_.get_().forDeclaration(getElement().getCeylonNode());
    }
}
