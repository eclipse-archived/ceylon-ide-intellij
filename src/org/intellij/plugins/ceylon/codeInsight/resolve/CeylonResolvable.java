package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CeylonResolvable extends CeylonCompositeElementImpl implements PsiNamedElement {

    public CeylonResolvable(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        TextRange range = TextRange.from(0, getTextLength());
        return new CeylonTypeReference<>(this, range, true);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not yet");
    }
}
