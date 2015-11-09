package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Value;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DeclarationPsiNameIdOwner extends CeylonPsiImpl.DeclarationPsiImpl implements PsiNameIdentifierOwner {
    public DeclarationPsiNameIdOwner(ASTNode astNode) {
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
        Tree.Declaration node = getCeylonNode();
        return node == null ? PsiTreeUtil.findChildOfType(this, CeylonPsi.IdentifierPsi.class) : CeylonTreeUtil.findPsiElement(node.getIdentifier(), getContainingFile());
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return ObjectUtils.notNull(getNameIdentifier(), this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        PsiElement id = findChildByType(CeylonTypes.IDENTIFIER);

        CeylonPsi.DeclarationPsi decl = CeylonTreeUtil.createDeclarationFromText(getProject(), "void " + name + "(){}");
        if (id != null) {
            id.replace(decl.getChildren()[0]);
        }
        return this;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        if (getCeylonNode().getScope() instanceof Value) {
            return new LocalSearchScope(getContainingFile());
        }

        return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope();
    }
}
