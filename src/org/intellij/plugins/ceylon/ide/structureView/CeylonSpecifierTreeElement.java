package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonDeclarationDescriptionProvider_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

class CeylonSpecifierTreeElement extends PsiTreeElementBase<CeylonPsi.SpecifierStatementPsi>
        implements ColoredItemPresentation {

    private ceylonDeclarationDescriptionProvider_ ceylonDeclarationDescriptionProvider =
            ceylonDeclarationDescriptionProvider_.get_();

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
        return toJavaString(
                ceylonDeclarationDescriptionProvider.getDescription(getElement(), false, false)
        );
    }
}
