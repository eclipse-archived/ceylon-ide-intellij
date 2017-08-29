package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.util.PsiTreeUtil.findChildOfType;

public class NamedStatementOrArgumentPsi extends CeylonPsiImpl.StatementOrArgumentPsiImpl
        implements PsiNameIdentifierOwner {

    public NamedStatementOrArgumentPsi(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        PsiElement id = getNameIdentifier();

        return id == null ? null : id.getText();
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();

        if (id != null) {
            return id.getTextOffset();
        }
        return super.getTextOffset();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        if (this instanceof CeylonPsi.ModuleDescriptorPsi) {
            Tree.ModuleDescriptor node = (Tree.ModuleDescriptor) super.getCeylonNode();
            return node == null
                    ? findChildOfType(this, CeylonPsi.IdentifierPsi.class)
                    : JavaTreeUtil.findPsiElement(node.getImportPath(), getContainingFile());
        } else if (this instanceof CeylonPsi.PackageDescriptorPsi) {
            Tree.PackageDescriptor node = (Tree.PackageDescriptor) super.getCeylonNode();
            return node == null
                    ? findChildOfType(this, CeylonPsi.IdentifierPsi.class)
                    : JavaTreeUtil.findPsiElement(node.getImportPath(), getContainingFile());
        }
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        if (this instanceof CeylonPsi.ModuleDescriptorPsi) {
            // already been renamed via a UsageInfo + ImportPathElementManipulator
            return this;
        }
        throw new UnsupportedOperationException("Not yet");
    }
}
