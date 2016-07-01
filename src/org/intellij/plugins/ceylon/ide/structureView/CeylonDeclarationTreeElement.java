package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.structureView.impl.java.AccessLevelProvider;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.ui.UIUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

abstract class CeylonDeclarationTreeElement<Decl extends CeylonPsi.DeclarationPsi>
        extends PsiTreeElementBase<Decl>
        implements ColoredItemPresentation, SortableTreeElement, AccessLevelProvider {

    private boolean isInherited;

    CeylonDeclarationTreeElement(Decl psiElement, boolean isInherited) {
        super(psiElement);
        this.isInherited = isInherited;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        CeylonDeclarationTreeElement that =
                (CeylonDeclarationTreeElement) o;
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

    ClassOrInterface getType() {
        Declaration model =
                getElement()
                    .getCeylonNode()
                    .getDeclarationModel();
        if (model!=null && model.isClassOrInterfaceMember()) {
            if (isInherited) {
                return (ClassOrInterface) model.getContainer();
            }
            Declaration refined = model.getRefinedDeclaration();
            if (refined!=null && !refined.equals(model)) {
                return (ClassOrInterface) refined.getContainer();
            }
        }
        return null;
    }

    @Override
    public String getLocationString() {
        if (!isValid()) {
            return null;
        }
        Declaration model =
                getElement()
                        .getCeylonNode()
                        .getDeclarationModel();
        if (model != null) {
            if (isInherited
                    && model.isClassOrInterfaceMember()) {
                ClassOrInterface container =
                        (ClassOrInterface)
                                model.getContainer();
                return " " + UIUtil.rightArrow() + container.getName();
            }
            Declaration refined =
                    model.getRefinedDeclaration();
            if (refined !=null &&
                !refined.equals(model)) {
                ClassOrInterface container =
                        (ClassOrInterface)
                                refined.getContainer();
                return " " + UIUtil.upArrow("^") + container.getName();
            }
        }
        return super.getLocationString();
    }

    @Nullable
    @Override
    public final String getPresentableText() {
        if (!isValid()) {
            return "INVALID";
        }
        return toJavaString(descriptions_.get_().descriptionForPsi(getElement(), false, false));
    }

    @Override
    public final Icon getIcon(boolean open) {
        if (!isValid()) {
            return null;
        }
        return icons_.get_().forDeclaration(getElement().getCeylonNode());
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        if (!isValid()) {
            return "ZZZZZ";
        }
        Tree.Identifier id = getElement().getCeylonNode().getIdentifier();
        return id==null ? "" : id.getText();
    }

    @Override
    public int getAccessLevel() {
        if (isInherited) {
            return PsiUtil.ACCESS_LEVEL_PUBLIC;
        }
        Declaration model =
                getElement()
                    .getCeylonNode()
                    .getDeclarationModel();
        if (model != null && model.isShared()) {
            return PsiUtil.ACCESS_LEVEL_PUBLIC;
        }
        Tree.AnnotationList annotationList =
                getElement()
                    .getCeylonNode()
                    .getAnnotationList();
        for (Tree.Annotation a: annotationList.getAnnotations()) {
            if (a.getPrimary().getText().equals("shared")) {
                return PsiUtil.ACCESS_LEVEL_PUBLIC;
            }
        }
        return PsiUtil.ACCESS_LEVEL_PRIVATE;
    }

    @Override
    public int getSubLevel() {
        return 0;
    }
}
