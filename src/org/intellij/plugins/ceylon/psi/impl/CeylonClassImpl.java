package org.intellij.plugins.ceylon.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.codeInsight.resolve.CeylonTypeReference;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonClassImpl extends CeylonClassDeclarationImpl implements CeylonClass {

    public CeylonClassImpl(ASTNode node) {
        super(node);
    }

    public CeylonClassImpl(ClassStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public boolean isInterface() {
        ASTNode kw = getNode().findChildByType(CeylonTypes.KW_INTERFACE);
        return kw != null;
    }

    @Nullable
    @Override
    public CeylonParameters getParameters() {
        if (isInterface()) {
            throw new UnsupportedOperationException("Interfaces are not parameterized");
        } else {
            return super.getParameters();
        }
    }

    @Nullable
    @Override
    public CeylonExtendedType getExtendedType() {
        if (isInterface()) {
            throw new UnsupportedOperationException("Interfaces don't have extended types");
        } else {
            return super.getExtendedType();
        }
    }

    @Nullable
    @Override
    public CeylonAdaptedTypes getAdaptedTypes() {
        if (isInterface()) {
            return findChildByClass(CeylonAdaptedTypes.class);
        }
        throw new UnsupportedOperationException("Only interfaces have adapted types");
    }

    @Nullable
    @Override
    public CeylonClassBody getClassBody() {
        if (isInterface()) {
            throw new UnsupportedOperationException("Only classes have class bodies");
        }
        return super.getClassBody();
    }

    @Override
    public CeylonInterfaceBody getInterfaceBody() {
        if (isInterface()) {
            return findChildByClass(CeylonInterfaceBody.class);
        }
        throw new UnsupportedOperationException("Only interfaces have interface bodies");
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
        } else {
            if (getContainingFile() instanceof CeylonFile) {
                return ((CeylonFile) getContainingFile()).getPackageName() + "." + getName();
            }
            return getName();
        }
    }

    @Override
    public CeylonClass getParentClass() {
        return PsiTreeUtil.getParentOfType(this, CeylonClass.class);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getTypeName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Can't set name yet");
    }

    @Override
    public PsiReference getReference() {
        PsiElement identifier = getNameIdentifier();

        if (identifier == null) {
            return null;
        } else {
            TextRange range = TextRange.from(0, identifier.getTextLength());
            return new CeylonTypeReference<CeylonIdentifier>((CeylonIdentifier) identifier, range, true);
        }
    }

    @Override
    public ItemPresentation getPresentation() {
        return ItemPresentationProviders.getItemPresentation(this);
    }
}
