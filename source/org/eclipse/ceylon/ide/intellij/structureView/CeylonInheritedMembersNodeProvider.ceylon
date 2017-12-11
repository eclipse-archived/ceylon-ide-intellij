/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
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
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import org.eclipse.ceylon.ide.common.completion {
    overloads
}
import org.eclipse.ceylon.ide.common.util {
    FindDeclarationNodeVisitor
}
import org.eclipse.ceylon.model.loader.model {
    FieldValue,
    JavaMethod,
    LazyClass
}
import org.eclipse.ceylon.model.typechecker.model {
    ...
}

import java.util {
    ArrayList,
    Collections,
    List
}

import org.eclipse.ceylon.ide.intellij.model {
    PSIClass,
    PSIMethod,
    concurrencyManager {
        outsideDumbMode
    },
    PsiElementGoneException
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonCompositeElement,
    CeylonFile,
    CeylonPsi,
    CeylonTreeUtil
}
import org.eclipse.ceylon.ide.intellij.resolve {
    resolveDeclaration
}

"Adds inherited members to the tree."
class CeylonInheritedMembersNodeProvider()
        extends InheritedMembersNodeProvider<TreeElement>()
        satisfies CeylonContainerTreeElement {

    provideNodes(TreeElement node)
            =>  if (is PsiTreeElementBase<out Anything> node,
                    is CeylonPsi.DeclarationPsi element = node.element,
                    exists declaration = element.ceylonNode,
                    exists type
                        = switch (declaration)
                        case (is Tree.ObjectDefinition)
                            declaration.declarationModel?.typeDeclaration
                        case (is Tree.ClassOrInterface)
                            declaration.declarationModel
                        else null)
            then scanMembers(element, declaration, type)
            else Collections.emptyList<TreeElement>();

    List<TreeElement> scanMembers(PsiElement element,
            Tree.Declaration declaration, TypeDeclaration type) {
        value elements = ArrayList<TreeElement>();
        value decls = type.getMatchingMemberDeclarations(declaration.unit, type, "", 0, null);

        for (dwp in decls.values()) {
            for (decl in overloads(dwp.declaration)) {
                if (!(decl is TypeParameter)) {
                    value unit = decl.unit;
                    value file =
                            unit==declaration.unit
                            then element.containingFile
                            else CeylonTreeUtil.getDeclaringFile(unit, element.project);

                    if (is CeylonFile file) {
                        if (exists treeElement
                                = getTreeElement {
                                    myFile = file;
                                    declaration = decl;
                                    inherited = type.isInherited(decl);
                                }) {
                            elements.add(treeElement);
                        }
                    } else {
                        try {
                            // perhaps a Java declaration?
                            if (is JavaMethod decl,
                                is PSIMethod mirror = decl.mirror) {
                                elements.add(PsiMethodTreeElement(mirror.psi, true));
                            } else if (decl is FieldValue,
                                is LazyClass scope = decl.scope,
                                is PSIClass mirror = scope.classMirror,
                                exists field = mirror.psi.findFieldByName(decl.name, true)) {
                                elements.add(PsiFieldTreeElement(field, true));
                            }
                        }
                        catch (PsiElementGoneException e) {}
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
        return outsideDumbMode(() {
            value visitor = FindDeclarationNodeVisitor(declaration);
            myFile.compilationUnit.visit(visitor);

            Node node;
            if (exists visitorNode = visitor.declarationNode) {
                node = visitorNode;
            }
            else if (is CeylonCompositeElement idOwner
                    = resolveDeclaration(declaration, myFile.project)) {
                node = idOwner.ceylonNode;
            }
            else {
                return null;
            }

            return if (is Tree.Declaration node)
                then getTreeElementForDeclaration {
                    myFile = myFile;
                    declaration = node;
                    isInherited = inherited;
                }
                else null;
        });
    }
}
