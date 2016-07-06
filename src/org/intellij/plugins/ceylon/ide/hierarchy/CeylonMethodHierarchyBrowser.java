package org.intellij.plugins.ceylon.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.types_;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.resolveDeclaration_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

class CeylonMethodHierarchyBrowser extends TypeHierarchyBrowserBase {

    private Project project;
    private final Set<PhasedUnit> modules;

    CeylonMethodHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
        this.project = project;
        modules = CeylonTypeHierarchyBrowser.collectPhasedUnits(project);
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName,
                                                                  @NotNull PsiElement psiElement) {
        @NotNull CeylonCompositeElement element = (CeylonCompositeElement) psiElement;
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
        if (psiElement instanceof CeylonCompositeElement) {
            CeylonCompositeElement psi =
                    (CeylonCompositeElement) psiElement;
            return getModel(psi).getQualifiedNameString();
        }
        else if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            return method.getContainingClass().getQualifiedName() + "." + method.getName();
        }
        else {
            return null;
        }
    }

    @Nullable
    @Override
    protected JPanel createLegendPanel() {
        //OK!
        return null;
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement psiElement) {
        return psiElement instanceof CeylonPsi.DeclarationPsi
            || psiElement instanceof CeylonPsi.SpecifierStatementPsi;
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

    private MethodHierarchyNodeDescriptor build(@NotNull PsiElement element, Declaration model) {
        if (model==null) {
            //TODO: should not really happen, but it does...
            return new MethodHierarchyNodeDescriptor(element, null);
        }
        Declaration refined = types_.get_().getRefinedDeclaration(model);
        if (refined == null || refined.equals(model)) {
            return new MethodHierarchyNodeDescriptor(element, model);
        }
        else {
            PsiElement psiElement = resolveDeclaration_.resolveDeclaration(refined, project);
            if (psiElement!=null) {
                MethodHierarchyNodeDescriptor parentDescriptor = build(psiElement, refined);
                MethodHierarchyNodeDescriptor nodeDescriptor =
                        new MethodHierarchyNodeDescriptor(parentDescriptor, element, model);
                parentDescriptor.children = new MethodHierarchyNodeDescriptor[] { nodeDescriptor };
                return nodeDescriptor;
            }
            else {
                return new MethodHierarchyNodeDescriptor(element, model);
            }
        }
    }

    private boolean directlyRefines(Declaration subtype, Declaration supertype) {
        List<Declaration> interveningRefinements
                = ModelUtil.getInterveningRefinements(
                    subtype.getName(),
                    ModelUtil.getSignature(subtype),
                    supertype.getRefinedDeclaration(),
                    (TypeDeclaration) subtype.getContainer(),
                    (TypeDeclaration) supertype.getContainer());
        interveningRefinements.remove(supertype);
        return interveningRefinements.isEmpty();
    }

    private class MethodHierarchyNodeDescriptor extends HierarchyNodeDescriptor {

        private final Declaration model; //TODO: put it in a weakref!
        private MethodHierarchyNodeDescriptor[] children;

        private String name(@NotNull PsiElement element) {
            if (element instanceof CeylonCompositeElement) {
                Node node = ((CeylonCompositeElement) element).getCeylonNode();
                if (node instanceof Tree.Declaration) {
                    return ((Tree.Declaration) node).getIdentifier().getText();
                } else if (node instanceof Tree.NamedArgument) {
                    return ((Tree.NamedArgument) node).getIdentifier().getText();
                } else if (node instanceof Tree.SpecifierStatement) {
                    return ((Tree.SpecifierStatement) node).getDeclaration().getName();
                } else {
                    return "<unknown>";
                }
            }
            else if (element instanceof PsiNamedElement) {
                return ((PsiNamedElement) element).getName();
            }
            else {
                return "<unknown>";
            }
        }

        private MethodHierarchyNodeDescriptor(@NotNull PsiElement element,
                                              Declaration model) {
            super(project, null, element, true);
            this.model = model;
            myName = name(element);
        }

        private MethodHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                              @NotNull PsiElement element,
                                              Declaration model) {
            super(project, parentDescriptor, element, false);
            this.model = model;
            myName = name(element);
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            PsiElement element = getPsiElement();
            CompositeAppearance oldText = myHighlightedText;
            label(element);
            if (!Comparing.equal(myHighlightedText, oldText)) {
                changes = true;
            }
            return changes;
        }

        private void label(PsiElement element) {
            if (element instanceof CeylonCompositeElement) {
                myHighlightedText = new CompositeAppearance();
                CeylonCompositeElement psi = (CeylonCompositeElement) element;
                appendDescription(psi);
                Node node = psi.getCeylonNode();
                appendNative(node);
                appendLocation(node);
            }
            else if (element instanceof PsiMethod) {
                myHighlightedText = new CompositeAppearance();
                PsiMethod psi = (PsiMethod) element;
                appendDescriptionAndLocation(psi);
            }
            else if (element instanceof NavigationItem) {
                myHighlightedText = new CompositeAppearance();
                NavigationItem psi = (NavigationItem) element;
                appendPresentation(psi);
            }
        }

        private void appendDescriptionAndLocation(PsiMethod psi) {
            PsiClass psiClass = psi.getContainingClass();
            highlighter_.get_()
                    .highlightCompositeAppearance(myHighlightedText,
                            "'" + psiClass.getName() + "." +
                                    psi.getPresentation().getPresentableText() + "'",
                            project);
            myHighlightedText.getEnding()
                    .addText("  " + psiClass.getPresentation().getLocationString(),
                            getPackageNameAttributes());
        }


        //TODO: refactor out code copy/pasted from CeylonTypeHierarchyBrowser

        private void appendPresentation(NavigationItem psi) {
            highlighter_.get_()
                    .highlightCompositeAppearance(myHighlightedText,
                            "'" + psi.getPresentation().getPresentableText() + "'",
                            project);
            myHighlightedText.getEnding()
                    .addText("  " + psi.getPresentation().getLocationString(),
                            getPackageNameAttributes());
        }

        private void appendDescription(CeylonCompositeElement psi) {
            String desc = toJavaString(descriptions_.get_().descriptionForPsi(psi, false, true, false, true));
            if (desc != null) {
                highlighter_.get_()
                        .highlightCompositeAppearance(myHighlightedText,
                                "'" + desc + "'", project);
            }
        }

        private void appendNative(Node node) {
            if (node instanceof Tree.Declaration) {
                Declaration dec = ((Tree.Declaration) node).getDeclarationModel();
                if (dec != null && dec.isNative()) {
                    myHighlightedText.getEnding()
                            .addText("  " + dec.getNativeBackends(),
                                    getPackageNameAttributes());
                }
            }
        }

        private void appendLocation(Node node) {
            String qualifiedNameString = null;
            Declaration dec = null;
            if (node instanceof Tree.Declaration) {
                Tree.Declaration decNode = (Tree.Declaration) node;
                dec = decNode.getDeclarationModel();
            }
            else if (node instanceof Tree.SpecifierStatement) {
                Tree.SpecifierStatement decNode = (Tree.SpecifierStatement) node;
                dec = decNode.getDeclaration();
            }
            Unit unit = node.getUnit();
            if (dec!=null) {
                if (dec.isClassOrInterfaceMember()) {
                    dec = (Declaration) dec.getContainer();
                }
                qualifiedNameString =
                        dec.getContainer()
                                .getQualifiedNameString();
            }
            else if (unit != null) {
                qualifiedNameString =
                        unit.getPackage()
                                .getQualifiedNameString();
            }
            if (qualifiedNameString!=null) {
                if (qualifiedNameString.isEmpty()) {
                    qualifiedNameString = "default package";
                }
                myHighlightedText.getEnding()
                        .addText("  (" + qualifiedNameString + ")",
                                getPackageNameAttributes());
            }
        }

    }


    private class SupertypesHierarchyTreeStructure extends HierarchyTreeStructure {

        private SupertypesHierarchyTreeStructure(@NotNull CeylonCompositeElement element) {
            super(CeylonMethodHierarchyBrowser.this.project,
                    new MethodHierarchyNodeDescriptor(element, getModel(element)));
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

    private class SubtypesHierarchyTreeStructure extends HierarchyTreeStructure {

        private SubtypesHierarchyTreeStructure(@NotNull CeylonCompositeElement element) {
            super(project, new MethodHierarchyNodeDescriptor(element, getModel(element)));
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

        private TypeHierarchyTreeStructure(@NotNull CeylonCompositeElement element) {
            super(project, build(element, getModel(element)));
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
        Declaration model = descriptor.model;
        if (model!=null
                && model.isClassOrInterfaceMember()
                && (model.isFormal() || model.isDefault())) {
            for (PhasedUnit unit : modules) {
                for (Declaration declaration : unit.getDeclarations()) {
                    if (declaration.isClassOrInterfaceMember() &&
                        declaration.isActual()) {
                        if (declaration.refines(model)
                                && !declaration.equals(model)
                                && directlyRefines(declaration, model)) {
                            PsiElement psiElement
                                    = resolveDeclaration_.resolveDeclaration(declaration, project);
                            if (psiElement!=null) {
                                result.add(new MethodHierarchyNodeDescriptor(descriptor,
                                        psiElement, declaration));
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
        Declaration model = descriptor.model;
        if (model!=null
                && model.isClassOrInterfaceMember()
                && model.isActual()) {
            ClassOrInterface container =
                    (ClassOrInterface) model.getContainer();
            List<Type> signature = ModelUtil.getSignature(model);
            for (Declaration supertype: container.getSupertypeDeclarations()) {
                Declaration declaration
                        = supertype.getDirectMember(model.getName(), signature, false, true);
                if (declaration!=null
                        && (declaration.isDefault() || declaration.isFormal())) {
                    if (model.refines(declaration)
                            && directlyRefines(model, declaration)) {
                        PsiElement psiElement
                                = resolveDeclaration_.resolveDeclaration(declaration, project);
                        if (psiElement!=null) {
                            result.add(new MethodHierarchyNodeDescriptor(descriptor,
                                    psiElement, declaration));
                        }
                    }
                }
            }
        }
        return result.toArray(new MethodHierarchyNodeDescriptor[0]);
    }

    private static Declaration getModel(PsiElement element) {
        if (element instanceof CeylonCompositeElement) {
            Node node = ((CeylonCompositeElement) element).getCeylonNode();
            if (node instanceof Tree.TypedDeclaration) {
                return ((Tree.TypedDeclaration) node).getDeclarationModel();
            } else if (node instanceof Tree.NamedArgument) {
                return ((Tree.TypedArgument) node).getDeclarationModel();
            } else if (node instanceof Tree.SpecifierStatement) {
                return ((Tree.SpecifierStatement) node).getDeclaration();
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

}
