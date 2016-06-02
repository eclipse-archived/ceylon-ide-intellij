package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.LocationPresentation;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.ui.UIUtil;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonDeclarationDescriptionProvider_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

abstract class CeylonDeclarationTreeElement<Decl extends CeylonPsi.DeclarationPsi> extends PsiTreeElementBase<Decl> implements ColoredItemPresentation, LocationPresentation {

    private boolean isInherited;
    private ceylonDeclarationDescriptionProvider_ ceylonDeclarationDescriptionProvider =
            ceylonDeclarationDescriptionProvider_.get_();

    CeylonDeclarationTreeElement(Decl psiElement, boolean isInherited) {
        super(psiElement);
        this.isInherited = isInherited;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @NotNull
    static String getParametersString(List<ParameterList> parameterLists, Unit unit) {
        StringBuilder builder = new StringBuilder();

        for (ParameterList parameterList : parameterLists) {
            builder.append("(");

            for (Parameter parameter : parameterList.getParameters()) {
                builder.append(parameter.getType().asString(unit)).append(", ");
            }

            if (builder.length() > 1) {
                builder.setLength(builder.length() - 2);
            }

            builder.append(")");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        final CeylonDeclarationTreeElement that = (CeylonDeclarationTreeElement) o;

        return isInherited == that.isInherited;
    }

    @Override
    public String toString() {
        return getPresentableText();
    }

    @Nullable
    @Override
    public TextAttributesKey getTextAttributesKey() {
        return isInherited ? CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES : null;
    }

    @Override
    public String getLocationString() {
        if (isInherited) {
            Declaration model = getElement().getCeylonNode().getDeclarationModel();

            if (model != null) {
                return UIUtil.rightArrow() + " " + model.getContainer().getQualifiedNameString();
            }
        }

        return super.getLocationString();
    }

    @Override
    public String getLocationSuffix() {
        return "";
    }

    @Override
    public String getLocationPrefix() {
        return " ";
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return toJavaString(
                ceylonDeclarationDescriptionProvider.getDescription(getElement(), false, false)
        );
    }
}
