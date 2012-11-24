package org.intellij.plugins.ceylon.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.codeInsight.resolve.CeylonTypeReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class CeylonIdentifier extends CeylonCompositeElementImpl implements PsiNamedElement {

    public CeylonIdentifier(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new CeylonTypeReference<CeylonIdentifier>(this, TextRange.from(0, getTextLength()), true);
    }

    @Override
    public String getName() {
        return getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }
}
