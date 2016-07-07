package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.IdeaCeylonParser;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonClassImpl extends StubBasedPsiElementBase<ClassStub> implements CeylonClass {
    private Tree.ClassOrInterface specClassDecl;
    private ClassStub stub;

    public CeylonClassImpl(ASTNode node) {
        super(node);
        specClassDecl =
                (Tree.ClassOrInterface)
                    node.getUserData(IdeaCeylonParser.CEYLON_NODE_KEY);
    }

    public CeylonClassImpl(ClassStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
        this.stub = stub;
    }

    public boolean isInterface() {
        return getNode().getElementType() == CeylonTypes.INTERFACE_DEFINITION;
    }

    @Override
    public String getName() {
        if (specClassDecl != null) {
            return specClassDecl.getIdentifier().getText();
        } else if (stub != null) {
            return stub.getName();
        }

        return null;
    }

    @Override
    public String getQualifiedName() {
        // TODO while indexing, the model is null, so we can't get the qualified name
        if (specClassDecl != null && specClassDecl.getDeclarationModel() != null) {
//            return specClassDecl.getDeclarationModel().getQualifiedNameString();
        } else if (stub != null) {
            return stub.getQualifiedName();
        }

        // bad workaround for the previous to do
        return getName();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(CeylonTypes.IDENTIFIER);
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return ObjectUtils.notNull(getNameIdentifier(), this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Can't set name yet");
    }

    @Override
    public Tree.ClassOrInterface getCeylonNode() {
        return specClassDecl;
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProviders.getItemPresentation(this);
    }

    @Override
    public String toString() {
        return (isInterface() ? "Interface " : "Class ")
                + ObjectUtils.notNull(getName(), " (unnamed)");
    }
}
