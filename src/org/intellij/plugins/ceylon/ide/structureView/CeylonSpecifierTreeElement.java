package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

import static org.intellij.plugins.ceylon.ide.structureView.CeylonDeclarationTreeElement.getParametersString;

class CeylonSpecifierTreeElement extends PsiTreeElementBase<CeylonPsi.SpecifierStatementPsi>
        implements ColoredItemPresentation {

    CeylonSpecifierTreeElement(CeylonPsi.SpecifierStatementPsi psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public TextAttributesKey getTextAttributesKey() {
        return null;
    }

    @Override
    public Icon getIcon(boolean open) {
        TypedDeclaration decl = getElement().getCeylonNode().getDeclaration();
        return decl == null ? null : ideaIcons_.get_().forDeclaration(decl);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        Tree.SpecifierStatement node = getElement().getCeylonNode();
        TypedDeclaration declaration = node.getDeclaration();
        if (declaration == null) {
            return "<unknown> (" + getElement().getText() + ")";
        }
        StringBuilder builder = new StringBuilder(declaration.getName());

        if (declaration instanceof Functional) {
            builder.insert(0, "function ");
            builder.append(getParametersString(((Functional) declaration).getParameterLists(), node.getUnit()));
        }

        return builder.toString();
    }
}
