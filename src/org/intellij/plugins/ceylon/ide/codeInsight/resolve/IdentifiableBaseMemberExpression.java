package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IdentifiableBaseMemberExpression extends CeylonPsiImpl.BaseMemberOrTypeExpressionPsiImpl implements PsiNamedElement {

    public IdentifiableBaseMemberExpression(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        return getCeylonNode().getIdentifier().getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }
}
