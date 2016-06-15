package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Shows the children declarations of a Ceylon file in the structure tool window.
 */
class CeylonFileTreeElement extends PsiTreeElementBase<CeylonFile>
        implements CeylonContainerTreeElement {

    private CeylonFile myElement;

    CeylonFileTreeElement(CeylonFile psiElement) {
        super(psiElement);
        myElement = psiElement;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        Tree.CompilationUnit compilationUnit = myElement.getCompilationUnit();
        List<Tree.Declaration> declarations = compilationUnit.getDeclarations();

        if (declarations.isEmpty()) {
            return Collections.emptyList();
        }

        List<StructureViewTreeElement> elements = new ArrayList<>();

        if (!compilationUnit.getImportList().getImports().isEmpty()) {
            CeylonPsi.ImportPsi[] imports = PsiTreeUtil.getChildrenOfType(
                    myElement.getFirstChild().getFirstChild(), CeylonPsi.ImportPsi.class);

            if (imports != null && imports.length > 0) {
                elements.add(new CeylonImportListTreeElement(imports));
            }
        }

        for (Tree.Declaration declaration : declarations) {
            StructureViewTreeElement node = getTreeElementForDeclaration(myElement, declaration, false);
            if (node != null) {
                elements.add(node);
            }
        }

        return elements;
    }

    static StructureViewTreeElement getTreeElementForDeclaration(CeylonFile myFile,
                                                                 Tree.Declaration declaration,
                                                                 boolean isInherited) {
        if (declaration == null) {
            return null;
        }

        if (declaration instanceof CustomTree.ClassOrInterface) {
            CeylonPsi.ClassOrInterfacePsi psiClass = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.ClassOrInterfacePsi.class);
            return new CeylonClassTreeElement(psiClass, isInherited);
        } else if (declaration instanceof Tree.AnyMethod) {
            CeylonPsi.AnyMethodPsi psiMethod = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.AnyMethodPsi.class);
            return new CeylonFunctionTreeElement(psiMethod, isInherited);
        } else if (declaration instanceof Tree.ObjectDefinition) {
            CeylonPsi.ObjectDefinitionPsi psiObject = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.ObjectDefinitionPsi.class);
            return new CeylonObjectTreeElement(psiObject, isInherited);
        } else if (declaration instanceof Tree.AnyAttribute) {
            CeylonPsi.AnyAttributePsi psiDecl = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.AnyAttributePsi.class);
            return new CeylonAttributeTreeElement(psiDecl, isInherited);
        } else if (declaration instanceof Tree.Variable) {
            CeylonPsi.VariablePsi psiDecl = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.VariablePsi.class);
            return new CeylonVariableTreeElement(psiDecl, isInherited);
        } else if (declaration instanceof Tree.Constructor) {
            CeylonPsi.ConstructorPsi psiDecl = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.ConstructorPsi.class);
            return new CeylonConstructorTreeElement(psiDecl);
        } else if (declaration instanceof Tree.Enumerated) {
            CeylonPsi.EnumeratedPsi psiDecl = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.EnumeratedPsi.class);
            return new CeylonEnumeratedTreeElement(psiDecl);
        } else if (declaration instanceof Tree.TypeAliasDeclaration) {
            CeylonPsi.TypeAliasDeclarationPsi psiDecl = PsiTreeUtil.getParentOfType(
                    myFile.findElementAt(declaration.getStartIndex()),
                    CeylonPsi.TypeAliasDeclarationPsi.class);
            return new CeylonTypeAliasTreeElement(psiDecl);
        }

        return null;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
