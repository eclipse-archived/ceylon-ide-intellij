package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.utilJ2C;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.concurrent.Callable;

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
        if (ConcurrencyManagerForJava.withAlternateResolution(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return ((CeylonFile) getContainingFile()).ensureTypechecked();
            }

        }) != null) {
            Declaration model = getCeylonNode().getDeclarationModel();

            if (model != null && !isAffectingOtherFiles(model)) {
                return new LocalSearchScope(getContainingFile());
            }
        }

        if (((CeylonFile) getContainingFile()).getPhasedUnit() instanceof ExternalPhasedUnit) {
            return ProjectScopeBuilder.getInstance(getProject()).buildAllScope()
                    .union(new LocalSearchScope(getContainingFile()));
        }

        return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope();
    }

    private boolean isAffectingOtherFiles(@NotNull  Declaration declaration) {
        if (declaration.isToplevel() || declaration.isShared()) {
            return true;
        }
        if (declaration.isParameter()) {
            FunctionOrValue fov = (FunctionOrValue) declaration;
            Declaration container = fov.getInitializerParameter().getDeclaration();

            if (container.isToplevel() || container.isShared()) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return utilJ2C.getIconForDeclaration(getCeylonNode());
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();

        if (id != null) {
            return id.getTextOffset();
        }
        return super.getTextOffset();
    }
}
