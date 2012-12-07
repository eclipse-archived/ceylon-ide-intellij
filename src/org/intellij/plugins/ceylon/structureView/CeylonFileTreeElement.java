package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonTypedMethodOrAttributeDeclaration;
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
        CeylonDeclaration[] declarations = myElement.findChildrenByClass(CeylonDeclaration.class);

        if (declarations.length == 0) {
            return Collections.emptyList();
        }

        List<StructureViewTreeElement> elements = new ArrayList<StructureViewTreeElement>();

        for (CeylonDeclaration declaration : declarations) {
            StructureViewTreeElement node = getTreeElementForDeclaration(declaration);
            if (node != null) {
                elements.add(node);
            }
        }

        return elements;
    }

    static StructureViewTreeElement getTreeElementForDeclaration(CeylonDeclaration declaration) {
        if (declaration == null) {
            return null;
        }

        for (PsiElement child : declaration.getChildren()) {
            if (child instanceof CeylonClass) {
                return new CeylonClassTreeElement((CeylonClass) child);
            } else if (child instanceof CeylonTypedMethodOrAttributeDeclaration) {
                return new CeylonMethodTreeElement((CeylonTypedMethodOrAttributeDeclaration) child);
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
