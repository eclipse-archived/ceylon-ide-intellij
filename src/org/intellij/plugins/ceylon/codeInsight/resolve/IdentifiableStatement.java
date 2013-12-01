package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Objects.firstNonNull;

public class IdentifiableStatement extends CeylonPsiImpl.StatementPsiImpl implements PsiNameIdentifierOwner {
    public IdentifiableStatement(ASTNode astNode) {
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
        return findChildByType(CeylonTypes.IDENTIFIER);
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return firstNonNull(getNameIdentifier(), this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
