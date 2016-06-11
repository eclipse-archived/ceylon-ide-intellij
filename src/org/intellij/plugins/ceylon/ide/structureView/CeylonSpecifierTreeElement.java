package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.ui.UIUtil;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
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

    private ceylonDeclarationDescriptionProvider_ provider =
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
    public String getLocationString() {
        Declaration model =
                getElement()
                        .getCeylonNode()
                        .getDeclaration();
        if (model != null) {
            Declaration refined =
                    model.getRefinedDeclaration();
            if (refined !=null &&
                    !refined.equals(model)) {
                ClassOrInterface container =
                        (ClassOrInterface)
                                refined.getContainer();
                return UIUtil.upArrow("^") + container.getName();
            }
        }
        return super.getLocationString();
    }

    /*@Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().forDeclaration(getElement().getCeylonNode());
    }*/

    @Nullable
    @Override
    public String getPresentableText() {
        return toJavaString(provider.getDescription(getElement(), false, false));
    }
}
