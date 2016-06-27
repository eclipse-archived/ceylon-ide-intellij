package org.intellij.plugins.ceylon.ide.hierarchy;

import ceylon.interop.java.JavaCollection;
import com.intellij.ide.hierarchy.HierarchyBrowserManager;
import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.hierarchy.HierarchyTreeStructure;
import com.intellij.ide.hierarchy.TypeHierarchyBrowserBase;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.SourceComparator;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.CompositeAppearance;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.descriptions_;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

public class CeylonTypeHierarchyBrowser extends TypeHierarchyBrowserBase {

    private Project project;
    private final Set<PhasedUnit> phasedUnits;

    CeylonTypeHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
        this.project = project;
        phasedUnits = collectPhasedUnits(project);
    }

    static Set<PhasedUnit> collectPhasedUnits(final Project project) {
        final Set<PhasedUnit> result = new HashSet<>();
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(new Runnable() {
             @Override
             public void run() {
                 ProgressIndicator indicator =
                         ProgressManager.getInstance().getProgressIndicator();
                 IdeaCeylonProjects ceylonProjects =
                         project.getComponent(IdeaCeylonProjects.class);
                 for (com.intellij.openapi.module.Module mod:
                         ModuleManager.getInstance(project).getModules()) {
                     indicator.setText("Indexing project " + mod.getName() + "...");
                     indicator.setIndeterminate(true);
                     CeylonProject ceylonProject = ceylonProjects.getProject(mod);
                     if (ceylonProject!=null) {
                         CeylonProject.Modules modules = ceylonProject.getModules();
                         Set<Module> listOfModules =
                                 modules.getTypecheckerModules().getListOfModules();
                         TypeChecker typechecker = ceylonProject.getTypechecker();
                         if (typechecker != null) {
                             //pick up stuff from edited source files
                             result.addAll(typechecker.getPhasedUnits().getPhasedUnits());
                         }
                         for (Module m : listOfModules) {
                             indicator.setText2("Indexing module " + m.getNameAsString());
                             IdeModule ideModule = (IdeModule) m;
                             result.addAll(new JavaCollection<PhasedUnit>(null,
                                     ideModule.getPhasedUnits().sequence()));
                         }
                     }
                 }
             }
         }, "Indexing Hierarchy", true, project);

        return result;
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String typeName, @NotNull PsiElement psiElement) {
        CeylonCompositeElement element = (CeylonCompositeElement) psiElement;
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
        CeylonCompositeElement psi =
                (CeylonCompositeElement) psiElement;
        return getModel(psi).getQualifiedNameString();
    }

    @Nullable
    @Override
    protected JPanel createLegendPanel() {
        //OK!
        return null;
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement psiElement) {
        return psiElement instanceof CeylonPsi.ClassOrInterfacePsi
             | psiElement instanceof CeylonPsi.ObjectDefinitionPsi
             | psiElement instanceof CeylonPsi.ObjectArgumentPsi
             | psiElement instanceof CeylonPsi.ObjectExpressionPsi;
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

    private TypeHierarchyNodeDescriptor build(CeylonCompositeElement element, Declaration dec) {
        if (dec instanceof ClassOrInterface) {
            ClassOrInterface model = (ClassOrInterface) dec;
            Type extendedType = model.getExtendedType();
            if (extendedType == null) {
                return new TypeHierarchyNodeDescriptor(element);
            } else {
                TypeDeclaration extended = extendedType.getDeclaration();
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(extended, project);
                if (psiElement instanceof CeylonPsi.ClassOrInterfacePsi) {
                    TypeHierarchyNodeDescriptor parentDescriptor =
                            build((CeylonPsi.ClassOrInterfacePsi) psiElement, extended);
                    if (parentDescriptor!=null) { //should not happen but it does!!
                        TypeHierarchyNodeDescriptor nodeDescriptor =
                                new TypeHierarchyNodeDescriptor(parentDescriptor, element);
                        parentDescriptor.children = new TypeHierarchyNodeDescriptor[]{nodeDescriptor};
                        return nodeDescriptor;
                    }
                }
                return new TypeHierarchyNodeDescriptor(element);
            }
        }
        else {
            return null;
        }
    }

    private static Declaration getModel(CeylonCompositeElement element) {
        Node node = element.getCeylonNode();
        Declaration dec;
        if (node instanceof Tree.ObjectDefinition) {
            dec = ((Tree.ObjectDefinition) node).getAnonymousClass();
        }
        else if (node instanceof Tree.ObjectExpression) {
            dec = ((Tree.ObjectExpression) node).getAnonymousClass();
        }
        else if (node instanceof Tree.Declaration) {
            dec = ((Tree.Declaration) node).getDeclarationModel();
        }
        else if (node instanceof Tree.ObjectArgument) {
            dec = ((Tree.ObjectArgument) node).getAnonymousClass();
        }
        else {
            return null;
        }
        if (dec instanceof Value) {
            Value value = (Value) dec;
            if (ModelUtil.isObject(value)) {
                dec = (value).getTypeDeclaration();
            }
        }
        return dec;
    }

    private class TypeHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
        private TypeHierarchyNodeDescriptor[] children;

        private TypeHierarchyNodeDescriptor(@NotNull CeylonCompositeElement element) {
            super(project, null, element, true);
            myName = name(element);
        }

        private TypeHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                            @NotNull CeylonCompositeElement element) {
            super(project, parentDescriptor, element, false);
            myName = name(element);
        }

        private String name(@NotNull CeylonCompositeElement element) {
            Node node = element.getCeylonNode();
            if (node instanceof Tree.Declaration) {
                return ((Tree.Declaration) node).getIdentifier().getText();
            }
            else if (node instanceof Tree.NamedArgument) {
                return ((Tree.NamedArgument) node).getIdentifier().getText();
            }
            else {
                return "object expression";
            }
        }

        private CeylonCompositeElement getDeclarationPsi() {
            return (CeylonCompositeElement) super.getPsiElement();
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            CeylonCompositeElement psi = getDeclarationPsi();
            icon(psi);
            CompositeAppearance oldText = myHighlightedText;
            label(psi);
            if (!Comparing.equal(myHighlightedText, oldText)) {
                changes = true;
            }
            return changes;
        }

        private void icon(@NotNull CeylonCompositeElement element) {
            if (element instanceof CeylonPsi.ObjectExpressionPsi) {
                setIcon(icons_.get_().getObjects());
            }
            else {
                //unnecessary
//                setIcon(icons_.get_().forDeclaration(element.getCeylonNode()));
            }
        }

        private void label(CeylonCompositeElement psi) {
            myHighlightedText = new CompositeAppearance();
            String desc = toJavaString(descriptions_.get_().descriptionForPsi(psi, false));
            if (desc == null) {
                myHighlightedText.getEnding()
                        .addText("object expression");
            }
            else {
                highlighter_.get_()
                        .highlightCompositeAppearance(myHighlightedText,
                                "'" + desc + "'", project);
            }
            Unit unit = psi.getCeylonNode().getUnit();
            if (unit!=null) {
                String qualifiedNameString =
                        unit.getPackage()
                            .getQualifiedNameString();
                myHighlightedText.getEnding()
                        .addText(" (" + qualifiedNameString + ")",
                            getPackageNameAttributes());
            }
        }
    }

    private class SupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
        private SupertypesHierarchyTreeStructure(CeylonCompositeElement element) {
            super(CeylonTypeHierarchyBrowser.this.project, new TypeHierarchyNodeDescriptor(element));
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            return aggregateSupertypes(parent, descriptor)
                    .toArray(new HierarchyNodeDescriptor[0]);
        }
    }

    private class SubtypesHierarchyTreeStructure extends HierarchyTreeStructure {

        private SubtypesHierarchyTreeStructure(CeylonCompositeElement element) {
            super(project, new TypeHierarchyNodeDescriptor(element));
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            TypeHierarchyNodeDescriptor[] children = aggregateSubtypes(descriptor);
            descriptor.children = children;
            return children;
        }
    }

    private class TypeHierarchyTreeStructure extends HierarchyTreeStructure {

        private TypeHierarchyTreeStructure(CeylonCompositeElement element) {
            super(project, build(element, getModel(element)));
            setBaseElement(myBaseDescriptor);
        }

        @NotNull
        @Override
        protected Object[] buildChildren(@NotNull HierarchyNodeDescriptor parent) {
            TypeHierarchyNodeDescriptor descriptor =
                    (TypeHierarchyNodeDescriptor) parent;
            if (descriptor.children!=null) {
                return descriptor.children;
            }
            TypeHierarchyNodeDescriptor[] children = aggregateSubtypes(descriptor);
            descriptor.children = children;
            return children;
        }

    }

    @NotNull
    private TypeHierarchyNodeDescriptor[] aggregateSubtypes(TypeHierarchyNodeDescriptor descriptor) {
        List<TypeHierarchyNodeDescriptor> result = new ArrayList<>();
        Declaration model = getModel(descriptor.getDeclarationPsi());
        if (model instanceof ClassOrInterface
                && !((ClassOrInterface) model).isFinal()) {
            for (PhasedUnit unit : phasedUnits) {
                for (Declaration declaration : unit.getDeclarations()) {
                    if (declaration instanceof ClassOrInterface) {
                        ClassOrInterface ci = (ClassOrInterface) declaration;
                        if (model instanceof Class) {
                            Type extendedType = ci.getExtendedType();
                            if (extendedType != null
                                    && extendedType.getDeclaration().equals(model)) {
                                PsiElement psiElement
                                        = CeylonReference.resolveDeclaration(ci, project);
                                if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                                    result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                            (CeylonPsi.DeclarationPsi) psiElement));
                                }
                            }
                        }
                        if (model instanceof Interface) {
                            for (Type satisfiedType : ci.getSatisfiedTypes()) {
                                if (satisfiedType != null
                                        && satisfiedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                                        result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                                (CeylonPsi.DeclarationPsi) psiElement));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result.toArray(new TypeHierarchyNodeDescriptor[0]);
    }

    @NotNull
    private List<HierarchyNodeDescriptor> aggregateSupertypes(@NotNull HierarchyNodeDescriptor parent,
                                                              TypeHierarchyNodeDescriptor descriptor) {
        List<HierarchyNodeDescriptor> result = new ArrayList<HierarchyNodeDescriptor>();
        Declaration model = getModel(descriptor.getDeclarationPsi());
        if (model instanceof ClassOrInterface) {
            ClassOrInterface ci = (ClassOrInterface) model;
            Type cl = ci.getExtendedType();
            if (cl != null) {
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(cl.getDeclaration(), project);
                //TODO: what about Java types in the hierarchy!!!!
                if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                    result.add(new TypeHierarchyNodeDescriptor(parent,
                            (CeylonPsi.DeclarationPsi) psiElement));
                }
            }
            for (Type type : ci.getSatisfiedTypes()) {
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(type.getDeclaration(), project);
                //TODO: what about Java types in the hierarchy!!!!
                if (psiElement instanceof CeylonPsi.DeclarationPsi) {
                    result.add(new TypeHierarchyNodeDescriptor(parent,
                            (CeylonPsi.DeclarationPsi) psiElement));
                }
            }
        }
        return result;
    }

}