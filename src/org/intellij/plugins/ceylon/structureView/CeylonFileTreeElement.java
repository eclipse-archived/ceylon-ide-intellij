package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CeylonFileTreeElement extends PsiTreeElementBase<CeylonFile> {

    private CeylonFile myElement;

    protected CeylonFileTreeElement(CeylonFile psiElement) {
        super(psiElement);
        myElement = psiElement;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        CeylonPsi.DeclarationPsi[] declarations = myElement.findChildrenByClass(CeylonPsi.DeclarationPsi.class);

        if (declarations.length == 0) {
            return Collections.emptyList();
        }

        List<StructureViewTreeElement> elements = new ArrayList<>();

        for (CeylonPsi.DeclarationPsi declaration : declarations) {
            StructureViewTreeElement node = getTreeElementForDeclaration(declaration);
            if (node != null) {
                elements.add(node);
            }
        }

        return elements;
    }

    static StructureViewTreeElement getTreeElementForDeclaration(CeylonPsi.DeclarationPsi declaration) {
        if (declaration == null) {
            return null;
        }

        for (PsiElement child : declaration.getChildren()) {
            if (child instanceof CeylonPsi.ClassOrInterfacePsi) {
                return new CeylonClassTreeElement((CeylonPsi.ClassOrInterfacePsi) child);
            } else if (child instanceof CeylonPsi.AnyMethodPsi) {
                return new CeylonMethodTreeElement((CeylonPsi.AnyMethodPsi) child);
            } else if (child instanceof CeylonPsi.TypedDeclarationPsi) {
                return new CeylonDeclarationTreeElement((CeylonPsi.TypedDeclarationPsi) child);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
