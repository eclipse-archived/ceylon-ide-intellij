package org.intellij.plugins.ceylon.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.yourkit.util.Strings;
import org.intellij.plugins.ceylon.parser.CeylonIdeaParser;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonClassImpl extends StubBasedPsiElementBase<ClassStub> implements CeylonClass {
    private Tree.ClassDeclaration specClassDecl;

    public CeylonClassImpl(ASTNode node) {
        super(node);
        specClassDecl = (Tree.ClassDeclaration) node.getUserData(CeylonIdeaParser.CEYLON_NODE_KEY);
    }

    public CeylonClassImpl(ClassStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

/*
    private boolean isInterface() {
        return false;
//        ASTNode kw = getNode().findChildByType(TokenTypes.INTERFACE_DEFINITION.getTokenType());
//        return kw != null;
    }
*/

    @Override
    public String getPackage() {
//        return specClassDecl.getUnit().getPackage().getQualifiedNameString();
        if (getContainingFile() instanceof CeylonFile) {
            return ((CeylonFile) getContainingFile()).getPackageName();
        }

        return null;
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();

        return (nameIdentifier == null) ? null : nameIdentifier.getText();
    }

    @Override
    public String getQualifiedName() {
        CeylonClass parentClass = PsiTreeUtil.getParentOfType(this, CeylonClass.class);

        if (parentClass != null) {
            return parentClass.getQualifiedName() + "." + getName();
        }

        String myPackage = getPackage();
        return myPackage == null ? getName() : myPackage + "." + getName();
    }

    @Override
    public CeylonClass getParentClass() {
        return PsiTreeUtil.getParentOfType(this, CeylonClass.class);
    }

    @Override
    public boolean isShared() {
        // todo!
        return false;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(CeylonTypes.UIDENTIFIER);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Can't set name yet");
    }

    @Override
    public Node getCeylonNode() {
        throw new UnsupportedOperationException("No Ceylon nodes in stubs.");
    }

    @Override
    public PsiReference getReference() {
        PsiElement identifier = getNameIdentifier();

//        if (identifier == null) {
            return null;
//        } else {
//            TextRange range = TextRange.from(0, identifier.getTextLength());
//            return new CeylonTypeReference<>((CeylonIdentifier) identifier, range, true);
//        }
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProviders.getItemPresentation(this);
    }

    @Override
    public String toString() {
        return Strings.notNull(getName(), " (unnamed)");
    }
}
