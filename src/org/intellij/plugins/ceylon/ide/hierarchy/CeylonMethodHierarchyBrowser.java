package org.intellij.plugins.ceylon.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.typechecker.model.*;
import com.redhat.ceylon.model.typechecker.model.Package;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.ceylonDeclarationDescriptionProvider_;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class CeylonMethodHierarchyBrowser extends TypeHierarchyBrowserBase {

    ceylonDeclarationDescriptionProvider_ provider =
            ceylonDeclarationDescriptionProvider_.get_();
    private Project project;

    CeylonMethodHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
        this.project = project;
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName, @NotNull PsiElement psiElement) {
        final CeylonPsi.TypedDeclarationPsi element =
                (CeylonPsi.TypedDeclarationPsi) psiElement;
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
        return psiElement instanceof CeylonPsi.TypedDeclarationPsi;
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

    private TypeHierarchyNodeDescriptor build(CeylonPsi.TypedDeclarationPsi element) {
        TypedDeclaration model = element.getCeylonNode().getDeclarationModel();
        if (model==null) {
            //TODO: should not really happen, but it does...
            return new TypeHierarchyNodeDescriptor(element);
        }
        Declaration refined = model.getRefinedDeclaration();
        if (refined == null || refined.equals(model)) {
            return new TypeHierarchyNodeDescriptor(element);
        }
        else {
            PsiElement psiElement
                    = CeylonReference.resolveDeclaration(refined, project);
            if (psiElement instanceof CeylonPsi.TypedDeclarationPsi) {
                TypeHierarchyNodeDescriptor parentDescriptor =
                        build((CeylonPsi.TypedDeclarationPsi) psiElement);
                TypeHierarchyNodeDescriptor nodeDescriptor =
                        new TypeHierarchyNodeDescriptor(parentDescriptor, element);
                parentDescriptor.children = new TypeHierarchyNodeDescriptor[] { nodeDescriptor };
                return nodeDescriptor;
            }
            else {
                return new TypeHierarchyNodeDescriptor(element);
            }
        }
    }

    private boolean directlyRefines(TypedDeclaration subtype, TypedDeclaration supertype) {
        List<Declaration> interveningRefinements
                = ModelUtil.getInterveningRefinements(
                    subtype.getName(),
                    ModelUtil.getSignature(subtype),
                    subtype.getRefinedDeclaration(),
                    (TypeDeclaration) subtype.getContainer(),
                    (TypeDeclaration) supertype.getContainer());
        return interveningRefinements.size()==1;
    }

    private class TypeHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
        private CeylonPsi.TypedDeclarationPsi element;
        private TypeHierarchyNodeDescriptor[] children;

        private TypeHierarchyNodeDescriptor(@NotNull CeylonPsi.TypedDeclarationPsi element) {
            super(project, null, element, true);
            this.element = element;
            myName = element.getCeylonNode().getIdentifier().getText();
        }

        private TypeHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                           @NotNull CeylonPsi.TypedDeclarationPsi element) {
            super(project, parentDescriptor, element, false);
            this.element = element;
            myName = element.getCeylonNode().getIdentifier().getText();
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            final CompositeAppearance oldText = myHighlightedText;
            myHighlightedText = new CompositeAppearance();
            String description =
                    "'" + provider.getDescription(element, false) + "'";
            highlighter_.get_()
                    .highlightCompositeAppearance(myHighlightedText, description, project);
            Unit unit = element.getCeylonNode().getUnit();
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
        private final Set<PhasedUnit> modules;

        private SupertypesHierarchyTreeStructure(CeylonPsi.TypedDeclarationPsi element) {
            super(CeylonMethodHierarchyBrowser.this.project, new TypeHierarchyNodeDescriptor(element));
            modules = CeylonTypeHierarchyBrowser.collectPhasedUnits(project);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor = (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
            TypedDeclaration model = descriptor.element.getCeylonNode().getDeclarationModel();
            if (model!=null && model.isClassOrInterfaceMember() && model.isActual()) {
                ClassOrInterface container = (ClassOrInterface) model.getContainer();
                Declaration refined = model.getRefinedDeclaration();
                List<Type> signature = ModelUtil.getSignature(model);
                for (Declaration types: container.getSupertypeDeclarations()) {
                    Declaration declaration
                            = types.getDirectMember(model.getName(), signature, false);
                    if (declaration instanceof TypedDeclaration &&
                            (declaration.isDefault() || declaration.isFormal())) {
                        TypedDeclaration candidate = (TypedDeclaration) declaration;
                        if (candidate.getRefinedDeclaration().equals(refined)
                                && directlyRefines(model, candidate)) {
                            PsiElement psiElement
                                    = CeylonReference.resolveDeclaration(candidate, project);
                            if (psiElement instanceof CeylonPsi.TypedDeclarationPsi) {
                                result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                        (CeylonPsi.TypedDeclarationPsi) psiElement));
                            }
                        }
                    }
                }
            }
            TypeHierarchyNodeDescriptor[] children
                    = result.toArray(new TypeHierarchyNodeDescriptor[0]);
            descriptor.children = children;
            return children;
        }
    }

    /*private TypeHierarchyNodeDescriptor root(TypeHierarchyNodeDescriptor child) {
        TypeHierarchyNodeDescriptor parentDescriptor =
                (TypeHierarchyNodeDescriptor)
                        child.getParentDescriptor();
        return parentDescriptor == null ? child : root(parentDescriptor);
    }*/

    private class SubtypesHierarchyTreeStructure extends HierarchyTreeStructure {
        private final Set<PhasedUnit> modules;

        private SubtypesHierarchyTreeStructure(CeylonPsi.TypedDeclarationPsi element) {
            super(project, new TypeHierarchyNodeDescriptor(element));
            modules = CeylonTypeHierarchyBrowser.collectPhasedUnits(project);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor = (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
            TypedDeclaration model = descriptor.element.getCeylonNode().getDeclarationModel();
            if (model!=null
                    && model.isClassOrInterfaceMember()
                    && (model.isFormal() || model.isDefault())) {
//                Declaration refined = model.getRefinedDeclaration();
                for (PhasedUnit unit : modules) {
                    for (Declaration declaration : unit.getDeclarations()) {
                        if (declaration instanceof TypedDeclaration &&
                                declaration.isClassOrInterfaceMember() &&
                                declaration.isActual()) {
                            TypedDeclaration candidate = (TypedDeclaration) declaration;
                            if (candidate.refines(model) && directlyRefines(candidate, model)) {
                                PsiElement psiElement
                                        = CeylonReference.resolveDeclaration(candidate, project);
                                if (psiElement instanceof CeylonPsi.TypedDeclarationPsi) {
                                    result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                            (CeylonPsi.TypedDeclarationPsi) psiElement));
                                }
                            }
                        }
                    }
                }
            }
            TypeHierarchyNodeDescriptor[] children
                    = result.toArray(new TypeHierarchyNodeDescriptor[0]);
            descriptor.children = children;
            return children;
        }
    }

    private class TypeHierarchyTreeStructure extends HierarchyTreeStructure {
        private final Set<PhasedUnit> modules;

        private TypeHierarchyTreeStructure(CeylonPsi.TypedDeclarationPsi element) {
            super(project, build(element));
            modules = CeylonTypeHierarchyBrowser.collectPhasedUnits(project);
            setBaseElement(myBaseDescriptor);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor = (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
            TypedDeclaration model = descriptor.element.getCeylonNode().getDeclarationModel();
            if (model!=null
                    && model.isClassOrInterfaceMember()
                    && (model.isFormal() || model.isDefault())) {
//                Declaration refined = model.getRefinedDeclaration();
                for (PhasedUnit unit : modules) {
                    for (Declaration declaration : unit.getDeclarations()) {
                        if (declaration instanceof TypedDeclaration &&
                                declaration.isClassOrInterfaceMember() &&
                                declaration.isActual()) {
                            TypedDeclaration candidate = (TypedDeclaration) declaration;
                            if (candidate.refines(model) && directlyRefines(candidate, model)) {
                                PsiElement psiElement
                                        = CeylonReference.resolveDeclaration(candidate, project);
                                if (psiElement instanceof CeylonPsi.TypedDeclarationPsi) {
                                    result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                            (CeylonPsi.TypedDeclarationPsi) psiElement));
                                }
                            }
                        }
                    }
                }
            }
            TypeHierarchyNodeDescriptor[] children
                    = result.toArray(new TypeHierarchyNodeDescriptor[0]);
            descriptor.children = children;
            return children;
        }
    }

}
