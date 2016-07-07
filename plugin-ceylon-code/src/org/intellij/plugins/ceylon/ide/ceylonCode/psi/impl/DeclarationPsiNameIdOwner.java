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
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.ide.common.util.escaping_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.utilJ2C;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class DeclarationPsiNameIdOwner
        extends CeylonPsiImpl.DeclarationPsiImpl
        implements PsiNameIdentifierOwner {

    public DeclarationPsiNameIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        CeylonPsi.IdentifierPsi identifierPsi
                = PsiTreeUtil.getChildOfType(this, CeylonPsi.IdentifierPsi.class);
        if (identifierPsi!=null) {
            return identifierPsi.getText();
        }
        else {
            return null;
        }
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Tree.Declaration node = getCeylonNode();
        if (node==null) {
            return null;
        }
        Tree.Identifier id = node.getIdentifier();
        try {
            return id == null ?
                    PsiTreeUtil.findChildOfType(this, CeylonPsi.IdentifierPsi.class) :
                    CeylonTreeUtil.findPsiElement(id, getContainingFile());
        }
        catch (IllegalArgumentException iae) {
            //this was happening with pattern variables in an anonymous function parameter list
            //(surely because I do some funny business with the AST in that case)
            return PsiTreeUtil.findChildOfType(this, CeylonPsi.IdentifierPsi.class);
        }
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        PsiElement id = findChildByType(CeylonTypes.IDENTIFIER);
        if (id != null) {
            String quoted = escaping_.get_().escape(name);
            CeylonPsi.DeclarationPsi decl =
                    CeylonTreeUtil.createDeclarationFromText(getProject(),
                            "void " + quoted + "(){}");
            id.replace(decl.getChildren()[0]);
        }
        return this;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        CeylonFile file = (CeylonFile) getContainingFile();
        ProjectScopeBuilder builder = ProjectScopeBuilder.getInstance(getProject());
        LocalAnalysisResult localAnalysisResult = file.getLocalAnalysisResult();
        if (localAnalysisResult != null) {
            if (localAnalysisResult.getUpToDate()) {
                Declaration model = getCeylonNode().getDeclarationModel();
                if (model!=null && !isAffectingOtherFiles(model)) {
                    return new LocalSearchScope(file);
                }
            }

            //TODO: is this really the best way to detect a library?
            if (localAnalysisResult.getLastPhasedUnit() instanceof ExternalPhasedUnit) {
                return new LocalSearchScope(file)
                        .union(builder.buildProjectScope())
                        .union(builder.buildLibrariesScope());
            }
        }

        return new LocalSearchScope(file).union(builder.buildProjectScope());
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
        return id != null ? id.getTextOffset() : super.getTextOffset();
    }
}
