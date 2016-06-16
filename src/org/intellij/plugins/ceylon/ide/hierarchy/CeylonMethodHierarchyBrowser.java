package org.intellij.plugins.ceylon.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.ide.common.util.types_;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class CeylonMethodHierarchyBrowser extends TypeHierarchyBrowserBase {

    private Project project;
    private final Set<PhasedUnit> modules;

    CeylonMethodHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
        this.project = project;
        modules = CeylonTypeHierarchyBrowser.collectPhasedUnits(project);
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName, @NotNull PsiElement psiElement) {
        final CeylonPsi.DeclarationPsi element =
                (CeylonPsi.DeclarationPsi) psiElement;
        if (SUPERTYPES_HIERARCHY_TYPE.equals(typeName)) {
            return new SupertypesHierarchyTreeStructure(element);
        }
        else if (SUBTYPES_HIERARCHY_TYPE.equals(typeName)) {
            return new SubtypesHierarchyTreeStructure(element);
        }
        else if (TYPE_HIERARCHY_TYPE.equals(typeName)) {
            return new TypeHierarchyTreeStructure(element);
        }
        else {
            return null;
        }
    }

    @Override
    protected void createTrees(@NotNull Map<String, JTree> trees) {
        createTreeAndSetupCommonActions(trees, IdeActions.GROUP_TYPE_HIERARCHY_POPUP);
    }

    @Nullable
    @Override
    protected PsiElement getElementFromDescriptor(
            @NotNull HierarchyNodeDescriptor hierarchyNodeDescriptor) {
        return hierarchyNodeDescriptor.getPsiElement();
    }

    @Override
    protected boolean isInterface(PsiElement psiElement) {
        return false;
//        return psiElement instanceof CeylonPsi.AnyInterfacePsi;
    }

    @Override
    protected boolean canBeDeleted(PsiElement psiElement) {
        return false;
    }

    @Override
    protected String getQualifiedName(PsiElement psiElement) {
        return ((CeylonPsi.DeclarationPsi) psiElement).getCeylonNode()
                .getDeclarationModel()
                .getQualifiedNameString();
    }

    @Nullable
    @Override
    protected JPanel createLegendPanel() {
        //OK!
        return null;
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement psiElement) {
        return psiElement instanceof CeylonPsi.DeclarationPsi;
    }

    @Nullable
    @Override
    protected Comparator<NodeDescriptor> getComparator() {
        if (HierarchyBrowserManager.getInstance(project)
                .getState().SORT_ALPHABETICALLY) {
            return AlphaComparator.INSTANCE;
        }
        else {
            //TODO: probably does not work!
            return SourceComparator.INSTANCE;
        }
    }

    private MethodHierarchyNodeDescriptor build(CeylonPsi.DeclarationPsi element) {
        Declaration model =
                element.getCeylonNode()
                    .getDeclarationModel();
        if (model==null) {
            //TODO: should not really happen, but it does...
            return new MethodHierarchyNodeDescriptor(element);
        }
        Declaration refined = types_.get_().getRefinedDeclaration(model);
        if (refined == null || refined.equals(model)) {
            return new MethodHierarchyNodeDescriptor(element);
        }
        else {
            PsiElement psiElement
                    = CeylonReference.resolveDeclaration(refined, project);
            if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                MethodHierarchyNodeDescriptor parentDescriptor =
                        build((CeylonPsi.DeclarationPsi) psiElement);
                MethodHierarchyNodeDescriptor nodeDescriptor =
                        new MethodHierarchyNodeDescriptor(parentDescriptor, element);
                parentDescriptor.children = new MethodHierarchyNodeDescriptor[] { nodeDescriptor };
                return nodeDescriptor;
            }
            else {
                return new MethodHierarchyNodeDescriptor(element);
            }
        }
    }

    private boolean directlyRefines(Declaration subtype, Declaration supertype) {
        List<Declaration> interveningRefinements
                = ModelUtil.getInterveningRefinements(
                    subtype.getName(),
                    ModelUtil.getSignature(subtype),
                    subtype.getRefinedDeclaration(),
                    (TypeDeclaration) subtype.getContainer(),
                    (TypeDeclaration) supertype.getContainer());
        return interveningRefinements.size()==1;
    }

    private class MethodHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
        private MethodHierarchyNodeDescriptor[] children;

        private MethodHierarchyNodeDescriptor(@NotNull CeylonPsi.DeclarationPsi element) {
            super(project, null, element, true);
            myName = element.getCeylonNode().getIdentifier().getText();
        }

        private MethodHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                              @NotNull CeylonPsi.DeclarationPsi element) {
            super(project, parentDescriptor, element, false);
            myName = element.getCeylonNode().getIdentifier().getText();
        }

        private CeylonPsi.DeclarationPsi getDeclarationPsi() {
            return (CeylonPsi.DeclarationPsi) super.getPsiElement();
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            final CompositeAppearance oldText = myHighlightedText;
            myHighlightedText = new CompositeAppearance();
            CeylonPsi.DeclarationPsi psi = getDeclarationPsi();
            String description =
                    "'" + descriptions_.get_().descriptionForPsi(psi, false) + "'";
            highlighter_.get_()
                    .highlightCompositeAppearance(myHighlightedText, description, project);
            Unit unit = psi.getCeylonNode().getUnit();
            if (unit!=null) {
                String qualifiedNameString =
                        unit.getPackage()
                            .getQualifiedNameString();
                myHighlightedText.getEnding()
                        .addText(" (" + qualifiedNameString + ")",
                            getPackageNameAttributes());
            }
            if (!Comparing.equal(myHighlightedText, oldText)) {
                changes = true;
            }
            return changes;
        }
    }

    private class SupertypesHierarchyTreeStructure extends HierarchyTreeStructure {

        private SupertypesHierarchyTreeStructure(CeylonPsi.DeclarationPsi element) {
            super(CeylonMethodHierarchyBrowser.this.project, new MethodHierarchyNodeDescriptor(element));
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            MethodHierarchyNodeDescriptor descriptor =
                    (MethodHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            MethodHierarchyNodeDescriptor[] children = aggregateSupertypes(descriptor);
            descriptor.children = children;
            return children;
        }
    }

    /*private MethodHierarchyNodeDescriptor root(MethodHierarchyNodeDescriptor child) {
        MethodHierarchyNodeDescriptor parentDescriptor =
                (MethodHierarchyNodeDescriptor)
                        child.getParentDescriptor();
        return parentDescriptor == null ? child : root(parentDescriptor);
    }*/

    private class SubtypesHierarchyTreeStructure extends HierarchyTreeStructure {

        private SubtypesHierarchyTreeStructure(CeylonPsi.DeclarationPsi element) {
            super(project, new MethodHierarchyNodeDescriptor(element));
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            MethodHierarchyNodeDescriptor descriptor =
                    (MethodHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            MethodHierarchyNodeDescriptor[] children = aggregateSubtypes(descriptor);
            descriptor.children = children;
            return children;
        }
    }

    private class TypeHierarchyTreeStructure extends HierarchyTreeStructure {

        private TypeHierarchyTreeStructure(CeylonPsi.DeclarationPsi element) {
            super(project, build(element));
            setBaseElement(myBaseDescriptor);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            MethodHierarchyNodeDescriptor descriptor =
                    (MethodHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            MethodHierarchyNodeDescriptor[] children = aggregateSubtypes(descriptor);
            descriptor.children = children;
            return children;
        }

    }

    @NotNull
    private MethodHierarchyNodeDescriptor[] aggregateSubtypes(MethodHierarchyNodeDescriptor descriptor) {
        List<MethodHierarchyNodeDescriptor> result = new ArrayList<>();
        Declaration model =
                descriptor.getDeclarationPsi()
                        .getCeylonNode()
                        .getDeclarationModel();
        if (model!=null
                && model.isClassOrInterfaceMember()
                && (model.isFormal() || model.isDefault())) {
            for (PhasedUnit unit : modules) {
                for (Declaration declaration : unit.getDeclarations()) {
                    if (declaration.isClassOrInterfaceMember() &&
                        declaration.isActual()) {
                        if (declaration.refines(model)
                                && directlyRefines(declaration, model)) {
                            PsiElement psiElement
                                    = CeylonReference.resolveDeclaration(declaration, project);
                            if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                                result.add(new MethodHierarchyNodeDescriptor(descriptor,
                                        (CeylonPsi.DeclarationPsi) psiElement));
                            }
                        }
                    }
                }
            }
        }
        return result.toArray(new MethodHierarchyNodeDescriptor[0]);
    }

    @NotNull
    private MethodHierarchyNodeDescriptor[] aggregateSupertypes(MethodHierarchyNodeDescriptor descriptor) {
        List<MethodHierarchyNodeDescriptor> result = new ArrayList<>();
        Declaration model =
                descriptor.getDeclarationPsi()
                        .getCeylonNode()
                        .getDeclarationModel();
        if (model!=null
                && model.isClassOrInterfaceMember()
                && model.isActual()) {
            ClassOrInterface container =
                    (ClassOrInterface) model.getContainer();
            Declaration refined = model.getRefinedDeclaration();
            List<Type> signature = ModelUtil.getSignature(model);
            for (Declaration supertype: container.getSupertypeDeclarations()) {
                Declaration declaration
                        = supertype.getDirectMember(model.getName(), signature, false);
                if (declaration!=null
                        && (declaration.isDefault() || declaration.isFormal())) {
                    if (declaration.getRefinedDeclaration().equals(refined)
                            && directlyRefines(model, declaration)) {
                        PsiElement psiElement
                                = CeylonReference.resolveDeclaration(declaration, project);
                        if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                            result.add(new MethodHierarchyNodeDescriptor(descriptor,
                                    (CeylonPsi.DeclarationPsi) psiElement));
                        }
                    }
                }
            }
        }
        return result.toArray(new MethodHierarchyNodeDescriptor[0]);
    }

}
