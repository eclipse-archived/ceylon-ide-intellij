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
import com.redhat.ceylon.ide.common.platform.Status;
import com.redhat.ceylon.ide.common.platform.platformUtils_;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.utilJ2C;
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
        Tree.Identifier id = getCeylonNode().getIdentifier();
        return id == null ? null : id.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Tree.Identifier id = getCeylonNode().getIdentifier();
        return id == null ?
                PsiTreeUtil.findChildOfType(this, CeylonPsi.IdentifierPsi.class) :
                CeylonTreeUtil.findPsiElement(id, getContainingFile());
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        PsiElement id = findChildByType(CeylonTypes.IDENTIFIER);
        CeylonPsi.DeclarationPsi decl =
                CeylonTreeUtil.createDeclarationFromText(getProject(),
                        "void " + name + "(){}");
        if (id != null) {
            id.replace(decl.getChildren()[0]);
        }
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
        return utilJ2C.getIconForDeclaration(getCeylonNode());
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getTextOffset() : super.getTextOffset();
    }
}
