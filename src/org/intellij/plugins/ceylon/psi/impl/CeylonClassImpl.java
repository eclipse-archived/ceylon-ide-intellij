package org.intellij.plugins.ceylon.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.apache.commons.lang.ObjectUtils;
import org.intellij.plugins.ceylon.parser.CeylonIdeaParser;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonClassImpl extends StubBasedPsiElementBase<ClassStub> implements CeylonClass {
    private Tree.ClassOrInterface specClassDecl;
    private ClassStub stub;

    public CeylonClassImpl(ASTNode node) {
        super(node);
        specClassDecl = (Tree.ClassOrInterface) node.getUserData(CeylonIdeaParser.CEYLON_NODE_KEY);
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
            return specClassDecl.getDeclarationModel().getQualifiedNameString();
        } else if (stub != null) {
            return stub.getQualifiedName();
        }

        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(CeylonTypes.IDENTIFIER);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Can't set name yet");
    }

    @Override
    public Tree.Declaration getCeylonNode() {
        return specClassDecl;
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
        return (isInterface() ? "Interface " : "Class ") + ObjectUtils.defaultIfNull(getName(), " (unnamed)");
    }
}
