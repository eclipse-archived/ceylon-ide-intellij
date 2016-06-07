package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class SpecifierStatementPsiIdOwner extends CeylonPsiImpl.SpecifierStatementPsiImpl
        implements PsiNameIdentifierOwner {

    public SpecifierStatementPsiIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        return getNameIdentifier().getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return CeylonTreeUtil.findPsiElement(getCeylonNode().getBaseMemberExpression(), getContainingFile());
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
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
            Declaration model = getCeylonNode().getDeclaration();

            if (model != null
                    && !(getCeylonNode().getScope() instanceof Tree.Package)
                    && !getCeylonNode().getDeclaration().isShared()) {

                return new LocalSearchScope(getContainingFile());
            }
        }

        if (((CeylonFile) getContainingFile()).getPhasedUnit() instanceof ExternalPhasedUnit) {
            return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope()
                    .union(new LocalSearchScope(getContainingFile()));
        }

        return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope();
    }
}
