package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.ide.common.typechecker.AnalysisResult;
import com.redhat.ceylon.ide.common.util.escaping_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.isInSourceArchive_;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.ide.psi.CeylonTreeUtil;
import org.intellij.plugins.ceylon.ide.util.utilJ2C;
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
        final PsiElement identifierPsi = getNameIdentifier();
        return getElementText(identifierPsi);
    }

    @Nullable
    public static String getElementText(final PsiElement identifierPsi) {
        return identifierPsi==null ? null :
                ApplicationManager.getApplication()
                        .runReadAction(new Computable<String>() {
                    @Override
                    public String compute() {
                        return identifierPsi.getText();
                    }
                });
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(CeylonPsi.IdentifierPsi.class);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        setElementName(name, getNameIdentifier(), getProject());
        return this;
    }

    public static void setElementName(@NonNls @NotNull String name, PsiElement id, Project project) {
        if (id != null) {
            String quoted = escaping_.get_().escape(name);
            CeylonPsi.DeclarationPsi decl =
                    CeylonTreeUtil.createDeclarationFromText(project,
                            "void " + quoted + "(){}");
            id.replace(decl.getChildren()[0]);
        }
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        CeylonFile file = (CeylonFile) getContainingFile();
        ProjectScopeBuilder builder = ProjectScopeBuilder.getInstance(getProject());
        AnalysisResult analysisResult = file.getAvailableAnalysisResult();
        if (analysisResult != null) {

            if (analysisResult.getUpToDate()) {
                Declaration model = getCeylonNode().getDeclarationModel();
                if (model!=null && !isAffectingOtherFiles(model)) {
                    return new LocalSearchScope(file);
                }
            }

            if (isInSourceArchive_.isInSourceArchive(file.realVirtualFile())) {
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
        return id == null ? super.getTextOffset() : id.getTextOffset();
    }
}
