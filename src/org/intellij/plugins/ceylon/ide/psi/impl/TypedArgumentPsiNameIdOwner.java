package org.eclipse.ceylon.ide.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.eclipse.ceylon.ide.intellij.psi.CeylonFile;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsi;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsiImpl;
import org.eclipse.ceylon.ide.intellij.util.icons_;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class TypedArgumentPsiNameIdOwner
        extends CeylonPsiImpl.TypedArgumentPsiImpl
        implements PsiNameIdentifierOwner {

    public TypedArgumentPsiNameIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        return DeclarationPsiNameIdOwner.getElementText(getNameIdentifier());
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(CeylonPsi.IdentifierPsi.class);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        DeclarationPsiNameIdOwner.setElementName(name, getNameIdentifier(), getProject());
        return this;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        //never called, AFAICT
        CeylonFile file = (CeylonFile) getContainingFile();
        return new LocalSearchScope(file);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return icons_.get_().forDeclaration(getCeylonNode());
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? super.getTextOffset() : id.getTextOffset();
    }
}
