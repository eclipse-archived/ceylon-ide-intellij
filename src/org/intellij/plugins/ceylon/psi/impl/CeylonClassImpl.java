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
import com.yourkit.util.Strings;
import org.intellij.plugins.ceylon.codeInsight.resolve.CeylonTypeReference;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.stub.ClassStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CeylonClassImpl extends StubBasedPsiElementBase<ClassStub> implements CeylonClass {

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

    @Override
    public boolean isObject() {
        return findChildByType(CeylonTypes.KW_OBJECT) != null;
    }

    @Override
    public String getPackage() {
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
        CeylonAnnotations annotations = ((CeylonDeclaration) getParent()).getAnnotations();

        for (CeylonAnnotation annotation : annotations.getAnnotationList()) {
            if (annotation.getAnnotationName().getText().equals("shared")) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiElement name = findChildByType(CeylonTypes.TYPE_NAME);

        if (name == null) {
            name = findChildByType(CeylonTypes.MEMBER_NAME);
        }

        return name;
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

    @Override
    public String toString() {
        String name = Strings.notNull(getName(), " (unnamed)");
        String type;

        if (isInterface()) {
            type = "Interface";
        } else {
            type = "Class";
        }

        return type + " " + name;
    }
}
