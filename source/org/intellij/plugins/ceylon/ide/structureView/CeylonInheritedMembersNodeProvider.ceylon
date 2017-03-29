import com.intellij.ide.structureView {
    StructureViewTreeElement
}
import com.intellij.ide.structureView.impl.common {
    PsiTreeElementBase
}
import com.intellij.ide.structureView.impl.java {
    PsiFieldTreeElement,
    PsiMethodTreeElement
}
import com.intellij.ide.util {
    InheritedMembersNodeProvider
}
import com.intellij.ide.util.treeView.smartTree {
    TreeElement
}
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    overloads
}
import com.redhat.ceylon.ide.common.util {
    FindDeclarationNodeVisitor
}
import com.redhat.ceylon.model.loader.model {
    FieldValue,
    JavaMethod,
    LazyClass
}
import com.redhat.ceylon.model.typechecker.model {
    ...
}

import java.util {
    ArrayList,
    Collection,
    Collections,
    List
}

import org.intellij.plugins.ceylon.ide.model {
    PSIClass,
    PSIMethod,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    CeylonTreeUtil
}
import org.intellij.plugins.ceylon.ide.resolve {
    resolveDeclaration
}

"Adds inherited members to the tree."
class CeylonInheritedMembersNodeProvider()
        extends InheritedMembersNodeProvider<TreeElement>()
        satisfies CeylonContainerTreeElement {

    shared actual Collection<TreeElement> provideNodes(TreeElement node) {
        if (is PsiTreeElementBase<out Anything> node) {
            if (is CeylonPsi.DeclarationPsi element = node.element) {
                value declaration = element.ceylonNode;
                if (exists type
                        = switch (declaration)
                        case (is Tree.ObjectDefinition)
                            declaration.declarationModel?.typeDeclaration
                        case (is Tree.ClassOrInterface)
                            declaration.declarationModel
                        else null) {
                    return scanMembers(element, declaration, type);
                }
                // else: Maybe the file hasn't been typechecked yet
            }
        }
        return Collections.emptyList<TreeElement>();
    }

    List<TreeElement> scanMembers(PsiElement element, Tree.Declaration declaration, TypeDeclaration type) {
        value elements = ArrayList<TreeElement>();
        value decls = type.getMatchingMemberDeclarations(declaration.unit, type, "", 0, null);

        for (DeclarationWithProximity dwp in decls.values()) {
            for (decl in overloads(dwp.declaration)) {
                if (!(decl is TypeParameter)) {
                    value unit = decl.unit;
                    value file = if (unit.equals(declaration.unit))
                    then element.containingFile
                    else CeylonTreeUtil.getDeclaringFile(unit, element.project);

                    if (is CeylonFile file) {
                        Boolean inherited = type.isInherited(decl);
                        if (exists treeElement = getTreeElement(file, decl, inherited)) {
                            elements.add(treeElement);
                        }
                    } else {
                        // perhaps a Java declaration?
                        if (is JavaMethod decl, is PSIMethod mirror = decl.mirror) {
                            elements.add(PsiMethodTreeElement(mirror.psi, true));
                        } else if (decl is FieldValue,
                            is LazyClass scope = decl.scope,
                            is PSIClass mirror = scope.classMirror,
                            exists field = mirror.psi.findFieldByName(decl.name, true)) {
                            elements.add(PsiFieldTreeElement(field, true));
                        }
                    }
                }
            }
        }
        return elements;
    }

    StructureViewTreeElement? getTreeElement(CeylonFile myFile,
        Declaration? declaration, Boolean inherited) {
        if (!exists declaration) {
            return null;
        }
        return concurrencyManager.outsideDumbMode(() {
                value visitor = FindDeclarationNodeVisitor(declaration);
                myFile.compilationUnit.visit(visitor);
                variable Node? node = visitor.declarationNode;
                if (!exists n = node) {
                    if (is CeylonCompositeElement idOwner = resolveDeclaration(declaration, myFile.project)) {
                        node = (idOwner).ceylonNode;
                    }
                }
                if (is Tree.Declaration n = node) {
                    return getTreeElementForDeclaration(myFile, n, inherited);
                }
                return null;
        });
    }
}
