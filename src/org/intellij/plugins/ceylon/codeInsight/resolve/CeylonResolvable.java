package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.psi.CeylonTokens;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CeylonResolvable extends CeylonCompositeElementImpl implements PsiNamedElement {

    public static final TokenSet UIDENT = TokenSet.create(CeylonTokens.UIDENTIFIER);

    public CeylonResolvable(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        final boolean isType = getNode().getChildren(UIDENT).length > 0;
        TextRange range = TextRange.from(0, getTextLength());
        return isType ? new CeylonTypeReference<>(this, range, true) : new CeylonReference<>(this, range, true);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not yet");
    }
}
