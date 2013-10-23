package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.structureView.CeylonFileTreeElement.getTreeElementForDeclaration;

public class CeylonClassTreeElement extends PsiTreeElementBase<CeylonPsi.ClassOrInterfacePsi> {
    private CeylonPsi.ClassOrInterfacePsi myClass;

    public CeylonClassTreeElement(CeylonPsi.ClassOrInterfacePsi element) {
        super(element);
        this.myClass = element;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        ASTNode blockNode = myClass.getNode().findChildByType(TokenSet.create(CeylonTypes.CLASS_BODY, CeylonTypes.INTERFACE_BODY));

        if (blockNode == null) {
            return Collections.emptyList();
        }

        CeylonPsi.BodyPsi block = (CeylonPsi.BodyPsi) blockNode.getPsi();
        List<StructureViewTreeElement> elements = new ArrayList<>();

        for (ASTNode statement : block.getNode().getChildren(TokenSet.create(CeylonTypes.STATEMENT))) {
            for (ASTNode declaration : statement.getChildren(TokenSet.create(CeylonTypes.DECLARATION))) {
                StructureViewTreeElement node = getTreeElementForDeclaration((CeylonPsi.DeclarationPsi) declaration.getPsi());
                if (node != null) {
                    elements.add(node);
                }
            }
        }

        return elements;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        ASTNode identifier = myClass.getNode().findChildByType(CeylonTypes.IDENTIFIER);
        return identifier == null ? "<unnamed class>" : identifier.getText();
    }

    @Override
    public Icon getIcon(boolean open) {
        return myClass instanceof CeylonPsi.AnyInterfacePsi ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
    }
}
