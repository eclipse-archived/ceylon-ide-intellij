package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Shows the children declarations of a Ceylon file in the structure tool window.
 */
public class CeylonFileTreeElement extends PsiTreeElementBase<CeylonFile> {

    private CeylonFile myElement;

    protected CeylonFileTreeElement(CeylonFile psiElement) {
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

        for (Tree.Declaration declaration : declarations) {
            StructureViewTreeElement node = getTreeElementForDeclaration(myElement, declaration);
            if (node != null) {
                elements.add(node);
            }
        }

        return elements;
    }

    static StructureViewTreeElement getTreeElementForDeclaration(CeylonFile myFile, Tree.Declaration declaration) {
        if (declaration == null) {
            return null;
        }

        if (declaration instanceof CustomTree.ClassOrInterface) {
            CeylonClass psiClass = PsiTreeUtil.getParentOfType(myFile.findElementAt(declaration.getStartIndex()), CeylonClass.class);
            return new CeylonClassTreeElement(psiClass);
        } else if (declaration instanceof Tree.AnyMethod) {
            CeylonPsi.AnyMethodPsi psiMethod = PsiTreeUtil.getParentOfType(myFile.findElementAt(declaration.getStartIndex()), CeylonPsi.AnyMethodPsi.class);
            return new CeylonMethodTreeElement(psiMethod);
        }

        // TODO support attributes?

        return null;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
