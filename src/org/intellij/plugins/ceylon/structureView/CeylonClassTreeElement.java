package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.lang.ASTNode;
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

public class CeylonClassTreeElement extends PsiTreeElementBase<CeylonClass> {
    private CeylonClass myClass;

    public CeylonClassTreeElement(CeylonClass element) {
        super(element);
        this.myClass = element;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        ASTNode blockNode = myClass.getNode().findChildByType(CeylonTypes.BLOCK);

        if (blockNode == null) {
            return Collections.emptyList();
        }

        CeylonBlock block = (CeylonBlock) blockNode.getPsi();
        List<StructureViewTreeElement> elements = new ArrayList<StructureViewTreeElement>();

        for (CeylonDeclarationOrStatement declarationOrStatement : block.getDeclarationOrStatementList()) {
            StructureViewTreeElement node = getTreeElementForDeclaration(declarationOrStatement.getDeclaration());
            if (node != null) {
                elements.add(node);
            }
        }

        return elements;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return myClass.getName();
    }

    @Override
    public Icon getIcon(boolean open) {
        return myClass.isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
    }
}
