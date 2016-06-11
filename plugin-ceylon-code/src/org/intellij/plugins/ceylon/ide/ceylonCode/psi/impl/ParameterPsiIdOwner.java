package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
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

    private Tree.Identifier getIdentifier() {
        Tree.Parameter node = getCeylonNode();
        if (node instanceof Tree.ParameterDeclaration) {
            Tree.ParameterDeclaration param = (Tree.ParameterDeclaration) node;
            return param.getTypedDeclaration().getIdentifier();
        } else if (node instanceof Tree.InitializerParameter) {
            Tree.InitializerParameter param = (Tree.InitializerParameter) node;
            return param.getIdentifier();
        }
        else {
            return null;
        }
    }

    @Override
    public String getName() {
        Tree.Identifier id = getIdentifier();
        return id == null ? null : id.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Tree.Identifier id = getIdentifier();
        return id == null ? null : findPsiElement(id, getContainingFile());
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }
}
