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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class CeylonDeclarationTreeElement<Decl extends CeylonPsi.DeclarationPsi> extends PsiTreeElementBase<Decl> implements ColoredItemPresentation, LocationPresentation {

    private boolean isInherited;

    protected CeylonDeclarationTreeElement(Decl psiElement, boolean isInherited) {
        super(psiElement);
        this.isInherited = isInherited;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    protected String getPresentableParameters() {
        Declaration model = getElement().getCeylonNode().getDeclarationModel();

        if (model instanceof Functional) {
            return getParametersString(((Functional) model).getParameterLists(), model.getUnit());
        }

        return "";
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

    @Nullable
    protected String getPresentableTypeParameters() {
        Declaration model = getElement().getCeylonNode().getDeclarationModel();

        if (model instanceof Generic && ((Generic) model).getTypeParameters().size() > 0) {
            StringBuilder builder = new StringBuilder("<");

            for (TypeParameter parameter : ((Generic) model).getTypeParameters()) {
                builder.append(parameter.getType().asString(model.getUnit())).append(", ");
            }

            if (builder.length() > 1) {
                builder.setLength(builder.length() - 2);
            }

            builder.append(">");

            return builder.toString();
        }

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        final CeylonDeclarationTreeElement that = (CeylonDeclarationTreeElement) o;

        return isInherited == that.isInherited;
    }

    @NotNull
    protected String getName() {
        Decl element = getElement();

        if (element != null) {
            if (element.getCeylonNode().getDeclarationModel() != null) {
                return element.getCeylonNode().getDeclarationModel().getName();
            } else {
                return element.getCeylonNode().getIdentifier().getText();
            }
        }

        return "<unknown>";
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
}
