package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParameterDeclarationPsiIdOwner extends CeylonPsiImpl.ParameterDeclarationPsiImpl implements PsiNameIdentifierOwner {
    public ParameterDeclarationPsiIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        PsiElement id = getNameIdentifier();

        return id == null ? null : id.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return CeylonTreeUtil.findPsiElement(getCeylonNode().getTypedDeclaration().getIdentifier(), getContainingFile());
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return ObjectUtils.notNull(getNameIdentifier(), this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }
}
