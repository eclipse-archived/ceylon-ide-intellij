package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;
import org.intellij.plugins.ceylon.ide.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Objects.firstNonNull;

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
        return firstNonNull(getNameIdentifier(), this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
//        Scope ceylonScope = getCeylonNode().getScope();
//
//        if (ceylonScope instanceof Declaration) {
//            FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor((Declaration) ceylonScope);
//            visitor.visit(((CeylonFile) getContainingFile()).getCompilationUnit());
//            Tree.Declaration decl = visitor.getDeclarationNode();
//            if (decl != null) {
//                PsiElement psiElt = PsiUtilCore.getElementAtOffset(getContainingFile(), decl.getStartIndex());
//                if (!(psiElt instanceof CeylonPsi.DeclarationPsi)) {
//                    psiElt = PsiTreeUtil.getParentOfType(psiElt, CeylonPsi.DeclarationPsi.class);
//                }
//                return new LocalSearchScope(psiElt);
//            }
//        }
//
//        return super.getUseScope();
        return new LocalSearchScope(getContainingFile());
    }
}
