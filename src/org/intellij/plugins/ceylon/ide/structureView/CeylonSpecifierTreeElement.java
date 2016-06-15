package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.structureView.impl.java.AccessLevelProvider;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.LocationPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ui.UIUtil;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

class CeylonSpecifierTreeElement extends PsiTreeElementBase<CeylonPsi.SpecifierStatementPsi>
        implements ColoredItemPresentation, SortableTreeElement, AccessLevelProvider {

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

    ClassOrInterface getType() {
        Declaration model =
                getElement()
                        .getCeylonNode()
                        .getDeclaration();
        if (model!=null && model.isClassOrInterfaceMember()) {
            Declaration refined = model.getRefinedDeclaration();
            if (refined!=null && !refined.equals(model)) {
                return (ClassOrInterface) refined.getContainer();
            }
        }
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

    @Nullable
    @Override
    public String getPresentableText() {
        return toJavaString(descriptions_.get_().descriptionForPsi(getElement(), false, false));
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        TypedDeclaration dec =
                getElement()
                    .getCeylonNode()
                    .getDeclaration();
        return dec==null ? "" : dec.getName();
    }

    @Override
    public int getAccessLevel() {
        return PsiUtil.ACCESS_LEVEL_PUBLIC;
    }

    @Override
    public int getSubLevel() {
        return 0;
    }
}
