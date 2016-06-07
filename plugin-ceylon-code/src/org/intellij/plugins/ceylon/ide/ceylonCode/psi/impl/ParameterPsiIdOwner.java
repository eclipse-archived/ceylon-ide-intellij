package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil.findPsiElement;

public abstract class ParameterPsiIdOwner extends CeylonPsiImpl.ParameterPsiImpl implements PsiNameIdentifierOwner {
    public ParameterPsiIdOwner(ASTNode astNode) {
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
        Tree.Parameter node = getCeylonNode();
        if (node instanceof Tree.ParameterDeclaration) {
            Tree.ParameterDeclaration param = (Tree.ParameterDeclaration) node;
            return findPsiElement(param.getTypedDeclaration().getIdentifier(), getContainingFile());
        } else if (node instanceof Tree.InitializerParameter) {
            Tree.InitializerParameter param = (Tree.InitializerParameter) node;
            return findPsiElement(param.getIdentifier(), getContainingFile());
        }

        return null;
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
