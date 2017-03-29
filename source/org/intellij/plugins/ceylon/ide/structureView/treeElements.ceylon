import ceylon.interop.java {
    javaClass
}

import com.intellij.ide.structureView {
    StructureViewTreeElement
}
import com.intellij.ide.structureView.impl.common {
    PsiTreeElementBase
}
import com.intellij.ide.structureView.impl.java {
    AccessLevelProvider
}
import com.intellij.ide.util.treeView.smartTree {
    SortableTreeElement
}
import com.intellij.navigation {
    ColoredItemPresentation
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil,
    PsiUtil
}
import com.intellij.util.ui {
    UIUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    CustomTree
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

import java.lang {
    Class,
    ObjectArray
}
import java.util {
    ArrayList,
    Collection,
    List,
    Collections
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonFile,
    descriptions
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

class CeylonAttributeTreeElement(CeylonPsi.AnyAttributePsi declaration, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.AnyAttributePsi>(declaration, isInherited) {
}

"A structure node which represents a CeylonClass (class or interface definition/declaration)."
class CeylonClassTreeElement(CeylonPsi.ClassOrInterfacePsi myClass, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.ClassOrInterfacePsi>(myClass, isInherited)
        satisfies CeylonContainerTreeElement {

    shared actual List<StructureViewTreeElement> childrenBase {
        value ceylonNode = myClass.ceylonNode;
        value elements = ArrayList<StructureViewTreeElement>();
        value bodyChildren
                = switch (ceylonNode)
                case (is CustomTree.ClassDefinition) ceylonNode.classBody.statements
                case (is Tree.InterfaceDefinition) ceylonNode.interfaceBody.statements
                else Collections.emptyList<Tree.Statement>();

        for (node in bodyChildren) {
            if (is Tree.Declaration node, is CeylonFile file = myClass.containingFile) {
                if (exists child = getTreeElementForDeclaration(file, node, false)) {
                    elements.add(child);
                }
            } else if (is Tree.SpecifierStatement node, node.refinement) {
                value spec = PsiTreeUtil.getParentOfType(
                    myClass.containingFile.findElementAt(node.startIndex.intValue()),
                    `CeylonPsi.SpecifierStatementPsi`
                );
                assert(exists spec);
                elements.add(CeylonSpecifierTreeElement(spec));
            }
        }
        return elements;
    }
}

class CeylonConstructorTreeElement(CeylonPsi.ConstructorPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.ConstructorPsi>(declaration, false) {
}

class CeylonEnumeratedTreeElement(CeylonPsi.EnumeratedPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.EnumeratedPsi>(declaration, false) {
}

"Shows the children declarations of a Ceylon file in the structure tool window."
class CeylonFileTreeElement(CeylonFile myElement)
        extends PsiTreeElementBase<CeylonFile>(myElement)
        satisfies CeylonContainerTreeElement {

    shared actual Collection<StructureViewTreeElement> childrenBase {
        value compilationUnit = myElement.compilationUnit;
        value declarations = compilationUnit.declarations;
        if (declarations.empty) {
            return Collections.emptyList<StructureViewTreeElement>();
        }
        value elements = ArrayList<StructureViewTreeElement>();
        if (!compilationUnit.importList.imports.empty) {
            value imports = PsiTreeUtil.getChildrenOfType(
                myElement.firstChild.firstChild,
                `CeylonPsi.ImportPsi`
            );
            if (exists imports, imports.size>0) {
                elements.add(CeylonImportListTreeElement(imports));
            }
        }
        for (declaration in declarations) {
            if (exists node = getTreeElementForDeclaration(myElement, declaration, false)) {
                elements.add(node);
            }
        }
        return elements;
    }

    presentableText => null;
}

class CeylonFunctionTreeElement(CeylonPsi.AnyMethodPsi declaration, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.AnyMethodPsi>(declaration, isInherited) {
}

class CeylonImportListTreeElement(ObjectArray<CeylonPsi.ImportPsi> imports)
        extends PsiTreeElementBase<PsiElement>(imports.get(0).parent)
        satisfies SortableTreeElement & AccessLevelProvider {

    shared actual Collection<StructureViewTreeElement> childrenBase {
        return Collections.emptyList<StructureViewTreeElement>();
    }

    presentableText => "Imports";

    getIcon(Boolean open) => icons.imports;

    alphaSortKey => "";

    accessLevel => PsiUtil.accessLevelPrivate;

    subLevel => 0;
}

class CeylonImportTreeElement(CeylonPsi.ImportPsi psiElement)
        extends PsiTreeElementBase<CeylonPsi.ImportPsi>(psiElement)
        satisfies SortableTreeElement & AccessLevelProvider {

    childrenBase => Collections.emptyList<StructureViewTreeElement>();

    presentableText
            => if (exists el = element)
                then el.text.substring(6, el.text.indexOf("{")).trimmed
                else "";

    getIcon(Boolean open) => icons.singleImport;

    alphaSortKey => element?.text else "";

    accessLevel => PsiUtil.accessLevelPrivate;

    subLevel => 0;
}

class CeylonObjectTreeElement(CeylonPsi.ObjectDefinitionPsi psiElement, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.ObjectDefinitionPsi>(psiElement, isInherited)
        satisfies CeylonContainerTreeElement {

    shared actual List<StructureViewTreeElement> childrenBase {
        value children = ArrayList<StructureViewTreeElement>();
        if (exists el = element) {
            for (statement in el.ceylonNode.classBody.statements) {
                switch (statement)
                case (is Tree.Declaration) {
                    if (is CeylonFile file = el.containingFile,
                        exists node = getTreeElementForDeclaration(file, statement, false)) {
                        children.add(node);
                    }
                }
                case (is Tree.SpecifierStatement) {
                    value spec = PsiTreeUtil.getParentOfType(
                        el.containingFile.findElementAt(statement.startIndex.intValue()),
                        `CeylonPsi.SpecifierStatementPsi`
                    );
                    assert(exists spec);
                    children.add(CeylonSpecifierTreeElement(spec));
                }
                else {}
            }
        }
        return children;
    }
}

class CeylonSpecifierTreeElement(CeylonPsi.SpecifierStatementPsi psiElement)
        extends PsiTreeElementBase<CeylonPsi.SpecifierStatementPsi>(psiElement)
        satisfies ColoredItemPresentation&SortableTreeElement&AccessLevelProvider {

    childrenBase => Collections.emptyList<StructureViewTreeElement>();

    textAttributesKey => null;

    shared ClassOrInterface? type {
        if (exists model = element?.ceylonNode?.declaration,
            model.classOrInterfaceMember,
            exists refined = model.refinedDeclaration,
            refined != model,
            is ClassOrInterface scope = refined.container) {

            return scope;
        }
        return null;
    }

    shared actual String? locationString {
        if (valid,
            exists model = element?.ceylonNode?.declaration,
            exists refined = model.refinedDeclaration,
            !refined.equals(model),
            is ClassOrInterface container = refined.container) {
            return UIUtil.upArrow("^") + container.name;
        }
        return super.locationString;
    }

    shared actual String? presentableText {
        if (!valid) {
            return "INVALID";
        }
        if (exists el = element,
            exists desc = descriptions.descriptionForPsi(el, false, false)) {
            return desc;
        }
        return null;
    }

    shared actual String alphaSortKey {
        if (!valid) {
            return "ZZZZZ";
        }
        return element?.ceylonNode?.declaration?.name else "";
    }

    accessLevel => PsiUtil.accessLevelPublic;

    subLevel => 0;
}

class CeylonTypeAliasTreeElement(CeylonPsi.TypeAliasDeclarationPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.TypeAliasDeclarationPsi>(declaration, false) {
}

class CeylonVariableTreeElement(CeylonPsi.VariablePsi declaration, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.VariablePsi>(declaration, isInherited) {
}

StructureViewTreeElement? getTreeElementForDeclaration(CeylonFile myFile,
    Tree.Declaration? declaration, Boolean isInherited) {

    if (!exists declaration) {
        return null;
    }

    function parentOfType<Type>(Class<Type> cls)
        given Type satisfies PsiElement {
        assert(exists parent = PsiTreeUtil.getParentOfType(myFile.findElementAt(declaration.startIndex.intValue()), cls));
        return parent;
    }

    switch (declaration)
    case (is CustomTree.ClassOrInterface) {
        value psiClass = parentOfType(javaClass<CeylonPsi.ClassOrInterfacePsi>());
        return CeylonClassTreeElement(psiClass, isInherited);
    }
    case (is Tree.AnyMethod) {
        value psiMethod = parentOfType(javaClass<CeylonPsi.AnyMethodPsi>());
        return CeylonFunctionTreeElement(psiMethod, isInherited);
    }
    case (is Tree.ObjectDefinition) {
        value psiObject = parentOfType(javaClass<CeylonPsi.ObjectDefinitionPsi>());
        return CeylonObjectTreeElement(psiObject, isInherited);
    }
    case (is Tree.AnyAttribute) {
        value psiDecl = parentOfType(javaClass<CeylonPsi.AnyAttributePsi>());
        return CeylonAttributeTreeElement(psiDecl, isInherited);
    }
    case (is Tree.Variable) {
        value psiDecl = parentOfType(javaClass<CeylonPsi.VariablePsi>());
        return CeylonVariableTreeElement(psiDecl, isInherited);
    }
    case (is Tree.Constructor) {
        value psiDecl = parentOfType(javaClass<CeylonPsi.ConstructorPsi>());
        return CeylonConstructorTreeElement(psiDecl);
    }
    case (is Tree.Enumerated) {
        value psiDecl = parentOfType(javaClass<CeylonPsi.EnumeratedPsi>());
        return CeylonEnumeratedTreeElement(psiDecl);
    }
    case (is Tree.TypeAliasDeclaration) {
        value psiDecl = parentOfType(javaClass<CeylonPsi.TypeAliasDeclarationPsi>());
        return CeylonTypeAliasTreeElement(psiDecl);
    }
    else {
        return null;
    }
}
