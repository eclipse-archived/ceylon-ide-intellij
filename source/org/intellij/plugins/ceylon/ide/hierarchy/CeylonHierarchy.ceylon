import com.intellij.ide.hierarchy {
    HierarchyNodeDescriptor,
    TypeHierarchyBrowserBase,
    HierarchyTreeStructure,
    HierarchyBrowserManager
}
import com.intellij.ide.util.treeView {
    AlphaComparator,
    SourceComparator
}
import com.intellij.navigation {
    NavigationItem
}
import com.intellij.openapi.actionSystem {
    IdeActions
}
import com.intellij.openapi.\imodule {
    ModuleManager
}
import com.intellij.openapi.progress {
    ProgressManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.roots.ui.util {
    CompositeAppearance
}
import com.intellij.openapi.util {
    Comparing
}
import com.intellij.psi {
    PsiNamedElement,
    PsiMethod,
    PsiElement,
    PsiClass
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.model {
    BaseIdeModule
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ClassOrInterface
}

import java.lang {
    ObjectArray,
    JString=String
}
import java.util {
    Set,
    HashSet,
    Map
}

import javax.swing {
    JTree
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.model {
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.psi {
    descriptions,
    CeylonCompositeElement,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared Set<PhasedUnit> collectPhasedUnits(Project project, Boolean sourcesOnly = false) {
    value result = HashSet<PhasedUnit>();
    ProgressManager.instance.runProcessWithProgressSynchronously(() {
            value indicator = ProgressManager.instance.progressIndicator;
            assert (exists ceylonProjects = getCeylonProjects(project));
            for (mod in ModuleManager.getInstance(project).modules) {
                indicator.checkCanceled();
                indicator.text = "Indexing project " + mod.name + "...";
                indicator.indeterminate = true;
                if (exists ceylonProject = ceylonProjects.getProject(mod),
                    exists modules = ceylonProject.modules) {
                    if (exists typechecker = ceylonProject.typechecker) {
                        result.addAll(typechecker.phasedUnits.phasedUnits);
                    }
                    if (!sourcesOnly) {
                        value listOfModules = modules.typecheckerModules.listOfModules;
                        for (m in listOfModules) {
                            indicator.checkCanceled();
                            indicator.text2 = "Indexing module " + m.nameAsString;
                            assert (is BaseIdeModule m );
                            m.phasedUnits.each(result.add);
                        }
                    }
                }
            }
            return "";
        }, "Indexing Hierarchy", true, project);
    return result;
}

class CeylonHierarchyNodeDescriptor(element, model, parent = null)
        extends HierarchyNodeDescriptor(element.project, parent, element, true) {

    CeylonHierarchyNodeDescriptor? parent;
    PsiElement element;
    shared Declaration? model;

    shared variable ObjectArray<CeylonHierarchyNodeDescriptor>? children = null;

    function name(PsiElement element) {
        switch (element)
        case (is CeylonCompositeElement) {
            switch (node = element.ceylonNode)
            case (is Tree.Declaration) {
                return node.identifier.text;
            }
            case (is Tree.NamedArgument) {
                return node.identifier.text;
            }
            case (is Tree.SpecifierStatement) {
                return node.declaration.name;
            }
            else {
                return "object expression";
            }
        }
        else case (is PsiNamedElement) {
            return element.name;
        }
        else {
            return "<unknown>";
        }
    }

    myName = name(element);

    shared actual Boolean update() {
        value changes = super.update();
        value oldText = myHighlightedText;
        icon(psiElement);
        label(psiElement);
        if (!Comparing.equal(myHighlightedText, oldText)) {
            return true;
        }
        return changes;
    }

    void icon(PsiElement? element) {
        if (is CeylonPsi.ObjectExpressionPsi element) {
            super.icon = icons.objects;
        }
    }

    void label(PsiElement? element) {
        switch (element)
        case (is CeylonCompositeElement) {
            myHighlightedText = CompositeAppearance();
            Node node = element.ceylonNode;
            appendDescription(element);
            appendNative(node);
            appendLocation(node);
        }
        else case (is PsiMethod) {
            myHighlightedText = CompositeAppearance();
            appendDescriptionAndLocation(element);
        }
        else case (is NavigationItem) {
            myHighlightedText = CompositeAppearance();
            appendPresentation(element);
        }
        else {}
    }

    void appendDescriptionAndLocation(PsiMethod psi) {
        if (exists text = psi.presentation?.presentableText,
            exists project = this.project) {
            assert (exists psiClass = psi.containingClass);
            highlighter.highlightCompositeAppearance(myHighlightedText,
                "'``(psiClass of PsiNamedElement).name else ""``.``text``'", project);
            if (exists loc = psiClass.presentation?.locationString) {
                myHighlightedText.ending.addText("  " + loc, packageNameAttributes);
            }
        }
    }

    void appendPresentation(NavigationItem psi) {
        if (exists text = psi.presentation?.presentableText,
            exists project = this.project) {
            highlighter.highlightCompositeAppearance(myHighlightedText, "'``text``'", project);
            if (exists loc = psi.presentation?.locationString) {
                myHighlightedText.ending.addText("  " + loc, packageNameAttributes);
            }
        }
    }

    void appendLocation(Node node) {
        Declaration? dec;
        switch (node)
        case (is Tree.Declaration) {
            dec = node.declarationModel;
        }
        case (is Tree.SpecifierStatement) {
            dec = node.declaration;
        }
        else {
            dec = null;
        }

        String qualifiedNameString;
        if (exists dec) {
            if (is ClassOrInterface container = dec.container) {
                qualifiedNameString = container.container.qualifiedNameString;
            }
            else {
                qualifiedNameString = dec.container.qualifiedNameString;
            }
        }
        else if (exists unit = node.unit) {
            qualifiedNameString = unit.\ipackage.qualifiedNameString;
        }
        else {
            return;
        }

        value name
                = qualifiedNameString.empty
        then "default package"
        else qualifiedNameString;
        myHighlightedText.ending.addText("  (``name``)",
            packageNameAttributes);
    }

    void appendNative(Node node) {
        if (is Tree.Declaration node,
            exists dec = node.declarationModel, dec.native) {
            myHighlightedText.ending.addText("  " + dec.nativeBackends.string,
                packageNameAttributes);
        }
    }

    void appendDescription(CeylonCompositeElement psi) {
        if (exists desc = descriptions.descriptionForPsi {
            element = psi;
            includeKeyword = false;
            includeContainer = true;
            includeReturnType = false;
            includeParameters = psi is CeylonPsi.AnyMethodPsi|CeylonPsi.SpecifierStatementPsi;
        },
            exists project = this.project) {
            highlighter.highlightCompositeAppearance(myHighlightedText, "'``desc``'", project);
        }
        else {
            myHighlightedText.ending.addText("object expression");
        }
    }

}

abstract class CeylonHierarchyBrowser(Project project, PsiElement element)
        extends TypeHierarchyBrowserBase(project, element) {

    shared ObjectArray<CeylonHierarchyNodeDescriptor> noDescriptors
            = ObjectArray<CeylonHierarchyNodeDescriptor>(0);

    shared Set<PhasedUnit> phasedUnits = collectPhasedUnits(project);

    shared actual String name => super.name;
    assign name => super.name = name;

    shared actual HierarchyTreeStructure? createHierarchyTreeStructure(String typeName, PsiElement psiElement) {
        assert (is CeylonCompositeElement element = psiElement);
        if (supertypesHierarchyType==typeName) {
            return SupertypesHierarchyTreeStructure(element);
        }
        else if (subtypesHierarchyType==typeName) {
            return SubtypesHierarchyTreeStructure(element);
        }
        else if (typeHierarchyType==typeName) {
            return TypeHierarchyTreeStructure(element);
        }
        else {
            return null;
        }
    }

    createTrees(Map<JString, JTree> trees)
            => createTreeAndSetupCommonActions(trees, IdeActions.groupTypeHierarchyPopup);

    getElementFromDescriptor(HierarchyNodeDescriptor hierarchyNodeDescriptor)
            => hierarchyNodeDescriptor.psiElement;

    isInterface(PsiElement psiElement) => false;

    canBeDeleted(PsiElement psiElement) => false;

    function getModel(PsiElement element) {
        if (is CeylonCompositeElement element) {
            switch (node = element.ceylonNode)
            case (is Tree.ObjectExpression) {
                return node.anonymousClass;
            }
            case (is Tree.TypeDeclaration) {
                return node.declarationModel;
            }
            case (is Tree.TypedDeclaration) {
                if (is Tree.ObjectDefinition node) {
                    return node.anonymousClass;
                }
                else {
                    return node.declarationModel;
                }
            }
            case (is Tree.ObjectArgument) {
                return node.anonymousClass;
            }
            case (is Tree.SpecifierStatement) {
                return node.declaration;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    shared actual String? getQualifiedName(PsiElement psiElement) {
        switch (psiElement)
        case (is CeylonCompositeElement) {
            return getModel(psiElement)?.qualifiedNameString;
        }
        else case (is PsiClass) {
            return psiElement.qualifiedName;
        }
        else case (is PsiMethod) {
            assert (exists containingClass = psiElement.containingClass,
                exists typeName = containingClass.qualifiedName);
            return typeName + "." + psiElement.name;
        }
        else {
            return null;
        }
    }

    createLegendPanel() => null;

    comparator
            => if (HierarchyBrowserManager.getInstance(project).state.sortAlphabetically)
    then AlphaComparator.instance
    else SourceComparator.instance;

    shared formal CeylonHierarchyNodeDescriptor? build(PsiElement element, Declaration? dec);

    shared formal ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSubtypes(CeylonHierarchyNodeDescriptor descriptor);

    shared formal ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSupertypes(CeylonHierarchyNodeDescriptor descriptor);

    class SupertypesHierarchyTreeStructure(CeylonCompositeElement element)
            extends HierarchyTreeStructure(outer.project,
        CeylonHierarchyNodeDescriptor(element, getModel(element))) {

        shared actual ObjectArray<Object> buildChildren(HierarchyNodeDescriptor parent) {
            assert (is CeylonHierarchyNodeDescriptor parent);
            return aggregateSupertypes(parent);
        }

    }
    class SubtypesHierarchyTreeStructure(CeylonCompositeElement element)
            extends HierarchyTreeStructure(project,
        CeylonHierarchyNodeDescriptor(element, getModel(element))) {

        shared actual ObjectArray<Object> buildChildren(HierarchyNodeDescriptor parent) {
            assert (is CeylonHierarchyNodeDescriptor parent);
            if (exists children = parent.children ) {
                return children;
            }
            value children = aggregateSubtypes(parent);
            parent.children = children;
            return children;
        }

    }

    class TypeHierarchyTreeStructure(CeylonCompositeElement element)
            extends HierarchyTreeStructure(project, build(element, getModel(element))) {
        setBaseElement(myBaseDescriptor);

        shared actual ObjectArray<Object> buildChildren(HierarchyNodeDescriptor parent) {
            assert (is CeylonHierarchyNodeDescriptor parent);
            if (exists children = parent.children) {
                return children;
            }
            value children = aggregateSubtypes(parent);
            parent.children = children;
            return children;
        }

    }

}