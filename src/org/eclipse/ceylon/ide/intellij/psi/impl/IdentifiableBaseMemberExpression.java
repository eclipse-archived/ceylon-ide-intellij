package org.eclipse.ceylon.ide.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsiImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class IdentifiableBaseMemberExpression extends CeylonPsiImpl.BaseMemberOrTypeExpressionPsiImpl implements PsiNamedElement {

    public IdentifiableBaseMemberExpression(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        Tree.BaseMemberOrTypeExpression node = getCeylonNode();
        return node == null ? null : node.getIdentifier().getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException();
    }
}
