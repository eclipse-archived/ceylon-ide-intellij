import ceylon.language.meta.model {
    ClassOrInterfaceModel=ClassOrInterface
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
        extends CeylonDeclarationTreeElement<CeylonPsi.AnyAttributePsi>
            (declaration, isInherited) {
}

"A structure node which represents a CeylonClass (class or interface definition/declaration)."
class CeylonClassTreeElement(CeylonPsi.ClassOrInterfacePsi myClass, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.ClassOrInterfacePsi>
            (myClass, isInherited)
        satisfies CeylonContainerTreeElement {

    shared actual List<StructureViewTreeElement> childrenBase {
        value ceylonNode = myClass.ceylonNode;
        value elements = ArrayList<StructureViewTreeElement>();
        value bodyChildren
                = switch (ceylonNode)
                case (is CustomTree.ClassDefinition)
                    ceylonNode.classBody.statements
                case (is Tree.InterfaceDefinition)
                    ceylonNode.interfaceBody.statements
                else Collections.emptyList<Tree.Statement>();

        for (node in bodyChildren) {
            if (is Tree.Declaration node,
                is CeylonFile file = myClass.containingFile) {
                if (exists child
                        = getTreeElementForDeclaration {
                            myFile = file;
                            declaration = node;
                            isInherited = false;
                        }) {
                    elements.add(child);
                }
            } else if (is Tree.SpecifierStatement node, node.refinement) {
                assert (exists spec
                        = PsiTreeUtil.getParentOfType(
                            myClass.containingFile.findElementAt(node.startIndex.intValue()),
                            `CeylonPsi.SpecifierStatementPsi`
                        ));
                elements.add(CeylonSpecifierTreeElement(spec));
            }
        }
        return elements;
    }
}

class CeylonConstructorTreeElement(CeylonPsi.ConstructorPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.ConstructorPsi>
            (declaration, false) {
}

class CeylonEnumeratedTreeElement(CeylonPsi.EnumeratedPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.EnumeratedPsi>
            (declaration, false) {
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
            if (exists node
                    = getTreeElementForDeclaration {
                        myFile = myElement;
                        declaration = declaration;
                        isInherited = false;
                    }) {
                elements.add(node);
            }
        }
        return elements;
    }

    presentableText => null;
}

class CeylonFunctionTreeElement(CeylonPsi.AnyMethodPsi declaration, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.AnyMethodPsi>
            (declaration, isInherited) {
}

class CeylonImportListTreeElement(ObjectArray<CeylonPsi.ImportPsi> imports)
        extends PsiTreeElementBase<PsiElement>
            (imports.get(0).parent)
        satisfies SortableTreeElement
                & AccessLevelProvider {

    childrenBase => Collections.emptyList<StructureViewTreeElement>();

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
        extends CeylonDeclarationTreeElement<CeylonPsi.ObjectDefinitionPsi>
            (psiElement, isInherited)
        satisfies CeylonContainerTreeElement {

    shared actual List<StructureViewTreeElement> childrenBase {
        value children = ArrayList<StructureViewTreeElement>();
        if (exists el = element, exists body = el.ceylonNode?.classBody) {
            for (statement in body.statements) {
                switch (statement)
                case (is Tree.Declaration) {
                    if (is CeylonFile file = el.containingFile,
                        exists node = getTreeElementForDeclaration(file, statement, false)) {
                        children.add(node);
                    }
                }
                case (is Tree.SpecifierStatement) {
                    assert (exists spec = PsiTreeUtil.getParentOfType(
                        el.containingFile.findElementAt(statement.startIndex.intValue()),
                        `CeylonPsi.SpecifierStatementPsi`
                    ));
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
        satisfies ColoredItemPresentation
                & SortableTreeElement
                & AccessLevelProvider {

    childrenBase => Collections.emptyList<StructureViewTreeElement>();

    textAttributesKey => null;

    shared ClassOrInterface? type
            =>  if (exists model = element?.ceylonNode?.declaration,
                    exists refined = model.refinedDeclaration,
                    refined != model,
                    is ClassOrInterface scope = refined.container)
            then scope
            else null;

    locationString
            => if (valid, exists scope = type)
            then UIUtil.upArrow("^") + scope.name
            else super.locationString;

    shared actual String? presentableText {
        if (!valid) {
            return "INVALID";
        }
        else if (
            exists el = element,
            exists desc
                    = descriptions.descriptionForPsi {
                        element = el;
                        includeKeyword = false;
                        includeContainer = false;
                    }) {
            return desc;
        }
        else {
            return null;
        }
    }

    alphaSortKey
            => !valid then "ZZZZZ"
            else element?.ceylonNode?.declaration?.name
            else "";

    accessLevel => PsiUtil.accessLevelPublic;

    subLevel => 0;
}

class CeylonTypeAliasTreeElement(CeylonPsi.TypeAliasDeclarationPsi declaration)
        extends CeylonDeclarationTreeElement<CeylonPsi.TypeAliasDeclarationPsi>
            (declaration, false) {
}

class CeylonVariableTreeElement(CeylonPsi.VariablePsi declaration, Boolean isInherited)
        extends CeylonDeclarationTreeElement<CeylonPsi.VariablePsi>
            (declaration, isInherited) {
}

StructureViewTreeElement? getTreeElementForDeclaration(CeylonFile myFile,
    Tree.Declaration? declaration, Boolean isInherited) {

    if (!exists declaration) {
        return null;
    }

    function parentOfType<Type>(ClassOrInterfaceModel<Type> classOrInterface)
        given Type satisfies PsiElement {
        assert (exists parent
                = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.startIndex.intValue()),
                    classOrInterface));
        return parent;
    }

    return switch (declaration)
        case (is CustomTree.ClassOrInterface)
            CeylonClassTreeElement {
                myClass = parentOfType(`CeylonPsi.ClassOrInterfacePsi`);
                isInherited = isInherited;
            }
        case (is Tree.AnyMethod)
            CeylonFunctionTreeElement {
                declaration = parentOfType(`CeylonPsi.AnyMethodPsi`);
                isInherited = isInherited;
            }
        case (is Tree.ObjectDefinition)
            CeylonObjectTreeElement {
                psiElement = parentOfType(`CeylonPsi.ObjectDefinitionPsi`);
                isInherited = isInherited;
            }
        case (is Tree.AnyAttribute)
            CeylonAttributeTreeElement {
                declaration = parentOfType(`CeylonPsi.AnyAttributePsi`);
                isInherited = isInherited;
            }
        case (is Tree.Variable)
            CeylonVariableTreeElement {
                declaration = parentOfType(`CeylonPsi.VariablePsi`);
                isInherited = isInherited;
            }
        case (is Tree.Constructor)
            CeylonConstructorTreeElement {
                declaration = parentOfType(`CeylonPsi.ConstructorPsi`);
            }
        case (is Tree.Enumerated)
            CeylonEnumeratedTreeElement {
                declaration = parentOfType(`CeylonPsi.EnumeratedPsi`);
            }
        case (is Tree.TypeAliasDeclaration)
            CeylonTypeAliasTreeElement {
                declaration = parentOfType(`CeylonPsi.TypeAliasDeclarationPsi`);
            }
        else null;
}
