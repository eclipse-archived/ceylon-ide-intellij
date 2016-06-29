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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
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
        if (psiElement instanceof CeylonCompositeElement) {
            CeylonCompositeElement psi =
                    (CeylonCompositeElement) psiElement;
            return getModel(psi).getQualifiedNameString();
        }
        else if (psiElement instanceof PsiClass) {
            return ((PsiClass) psiElement).getQualifiedName();
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

    private TypeHierarchyNodeDescriptor build(PsiElement element, Declaration dec) {
        if (dec instanceof ClassOrInterface) {
            ClassOrInterface model = (ClassOrInterface) dec;
            Type extendedType = model.getExtendedType();
            if (extendedType == null) {
                return new TypeHierarchyNodeDescriptor(element, model);
            } else {
                TypeDeclaration extended = extendedType.getDeclaration();
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(extended, project);
                TypeHierarchyNodeDescriptor parentDescriptor =
                        build(psiElement, extended);
                if (parentDescriptor!=null) { //should not happen but it does!!
                    TypeHierarchyNodeDescriptor nodeDescriptor =
                            new TypeHierarchyNodeDescriptor(parentDescriptor, element, model);
                    parentDescriptor.children = new TypeHierarchyNodeDescriptor[]{nodeDescriptor};
                    return nodeDescriptor;
                }
                return new TypeHierarchyNodeDescriptor(element, model);
            }
        }
        else {
            return null;
        }
    }

    private static TypeDeclaration getModel(PsiElement element) {
        if (element instanceof CeylonCompositeElement) {
            Node node = ((CeylonCompositeElement) element).getCeylonNode();
            TypeDeclaration dec;
            if (node instanceof Tree.ObjectDefinition) {
                return ((Tree.ObjectDefinition) node).getAnonymousClass();
            } else if (node instanceof Tree.ObjectExpression) {
                return ((Tree.ObjectExpression) node).getAnonymousClass();
            } else if (node instanceof Tree.TypeDeclaration) {
                return ((Tree.TypeDeclaration) node).getDeclarationModel();
            } else if (node instanceof Tree.ObjectArgument) {
                return ((Tree.ObjectArgument) node).getAnonymousClass();
            } else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private class TypeHierarchyNodeDescriptor extends HierarchyNodeDescriptor {
        private final TypeDeclaration model; //TODO: put it in a weakref!
        private TypeHierarchyNodeDescriptor[] children;

        private TypeHierarchyNodeDescriptor(@NotNull PsiElement element, @NotNull TypeDeclaration model) {
            super(project, null, element, true);
            this.model = model;
            myName = name(element);
        }

        private TypeHierarchyNodeDescriptor(@NotNull NodeDescriptor parentDescriptor,
                                            @NotNull PsiElement element,
                                            @NotNull TypeDeclaration model) {
            super(project, parentDescriptor, element, false);
            this.model = model;
            myName = name(element);
        }

        private String name(@NotNull PsiElement element) {
            if (element instanceof CeylonCompositeElement) {
                Node node = ((CeylonCompositeElement) element).getCeylonNode();
                if (node instanceof Tree.Declaration) {
                    return ((Tree.Declaration) node).getIdentifier().getText();
                } else if (node instanceof Tree.NamedArgument) {
                    return ((Tree.NamedArgument) node).getIdentifier().getText();
                } else {
                    return "object expression";
                }
            }
            else if (element instanceof PsiNamedElement) {
                return ((PsiNamedElement) element).getName();
            }
            else {
                return "<unknown>";
            }
        }

        @Override
        public boolean update() {
            boolean changes = super.update();
            PsiElement element = getPsiElement();
            icon(element);
            CompositeAppearance oldText = myHighlightedText;
            label(element);
            if (!Comparing.equal(myHighlightedText, oldText)) {
                changes = true;
            }
            return changes;
        }

        private void icon(@NotNull PsiElement element) {
            if (element instanceof CeylonPsi.ObjectExpressionPsi) {
                setIcon(icons_.get_().getObjects());
            }
        }

        private void label(PsiElement element) {
            myHighlightedText = new CompositeAppearance();
            String desc;
            if (element instanceof CeylonCompositeElement) {
                CeylonCompositeElement psi = (CeylonCompositeElement) element;
                desc = toJavaString(descriptions_.get_().descriptionForPsi(psi, false));
            }
            else if (element instanceof PsiNamedElement) {
                desc = ((PsiNamedElement) element).getName();
            }
            else {
                desc = null;
            }
            if (desc == null) {
                myHighlightedText.getEnding()
                        .addText("object expression");
            }
            else {
                highlighter_.get_()
                        .highlightCompositeAppearance(myHighlightedText,
                                "'" + desc + "'", project);
            }
            if (element instanceof CeylonCompositeElement) {
                CeylonCompositeElement psi = (CeylonCompositeElement) element;
                Unit unit = psi.getCeylonNode().getUnit();
                if (unit != null) {
                    String qualifiedNameString =
                            unit.getPackage()
                                    .getQualifiedNameString();
                    if (qualifiedNameString==null || qualifiedNameString.isEmpty()) {
                        qualifiedNameString = "default package";
                    }
                    myHighlightedText.getEnding()
                            .addText(" (" + qualifiedNameString + ")",
                                    getPackageNameAttributes());
                }
            }
        }
    }

    private class SupertypesHierarchyTreeStructure extends HierarchyTreeStructure {
        private SupertypesHierarchyTreeStructure(CeylonCompositeElement element) {
            super(CeylonTypeHierarchyBrowser.this.project,
                    new TypeHierarchyNodeDescriptor(element, getModel(element)));
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
            super(project, new TypeHierarchyNodeDescriptor(element, getModel(element)));
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
        Declaration model = descriptor.model;
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
                                result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                        psiElement, ci));
                            }
                        }
                        if (model instanceof Interface) {
                            for (Type satisfiedType : ci.getSatisfiedTypes()) {
                                if (satisfiedType != null
                                        && satisfiedType.getDeclaration().equals(model)) {
                                    PsiElement psiElement
                                            = CeylonReference.resolveDeclaration(ci, project);
                                    result.add(new TypeHierarchyNodeDescriptor(descriptor,
                                            psiElement, ci));
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
        Declaration model = descriptor.model;
        if (model instanceof ClassOrInterface) {
            ClassOrInterface ci = (ClassOrInterface) model;
            Type cl = ci.getExtendedType();
            if (cl != null) {
                TypeDeclaration td = cl.getDeclaration();
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(td, project);
                result.add(new TypeHierarchyNodeDescriptor(parent, psiElement, td));
            }
            for (Type type : ci.getSatisfiedTypes()) {
                TypeDeclaration td = type.getDeclaration();
                PsiElement psiElement
                        = CeylonReference.resolveDeclaration(td, project);
                result.add(new TypeHierarchyNodeDescriptor(parent, psiElement, td));
            }
        }
        return result;
    }

}