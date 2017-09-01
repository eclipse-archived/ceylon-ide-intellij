package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.platform.Status;
import com.redhat.ceylon.ide.common.platform.platformUtils_;
import com.redhat.ceylon.ide.common.typechecker.AnalysisResult;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.ide.psi.isInSourceArchive_;
import org.intellij.plugins.ceylon.ide.util.icons_;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;

public class SpecifierStatementPsiIdOwner extends CeylonPsiImpl.SpecifierStatementPsiImpl
        implements PsiNameIdentifierOwner {

    public SpecifierStatementPsiIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        return DeclarationPsiNameIdOwner.getElementText(getNameIdentifier());
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        CeylonPsi.BaseMemberExpressionPsi baseExpr;
        CeylonPsi.ParameterizedExpressionPsi pExpr
                = findChildByClass(CeylonPsi.ParameterizedExpressionPsi.class);

        if (pExpr != null) {
            baseExpr = findChildOfType(pExpr, CeylonPsi.BaseMemberExpressionPsi.class);
        } else {
            baseExpr = findChildByClass(CeylonPsi.BaseMemberExpressionPsi.class);
        }
        if (baseExpr != null) {
            return baseExpr.getChildren()[0];
        }
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        DeclarationPsiNameIdOwner.setElementName(name, getNameIdentifier(), getProject());
        return this;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return icons_.get_().forDeclaration(getCeylonNode());
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        CeylonFile ceylonFile = (CeylonFile) getContainingFile();
        AnalysisResult localAnalysisResult = ceylonFile.getAvailableAnalysisResult();
        if (localAnalysisResult != null) {
            if (localAnalysisResult.getUpToDate()) {
                Declaration model = getCeylonNode().getDeclaration();

                if (model != null
                        && !(getCeylonNode().getScope() instanceof Tree.Package)
                        && !getCeylonNode().getDeclaration().isShared()) {

                    return new LocalSearchScope(getContainingFile());
                }
            } else {
                platformUtils_.get_().log(Status.getStatus$_DEBUG(),
                        "Local scope not added in getUseScope() because the file "
                                + getContainingFile()
                                + " is not typechecked and up-to-date");
                Exception e = platformUtils_.get_().newOperationCanceledException();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                else {
                    throw new RuntimeException(e);
                }
            }

            if (isInSourceArchive_.isInSourceArchive(ceylonFile.realVirtualFile())) {
                return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope()
                        .union(new LocalSearchScope(getContainingFile()));
            }
        }

        return ProjectScopeBuilder.getInstance(getProject()).buildProjectScope();
    }
}
