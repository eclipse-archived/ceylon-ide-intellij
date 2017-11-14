/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsi;
import org.eclipse.ceylon.ide.intellij.psi.CeylonPsiImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParameterPsiIdOwner extends CeylonPsiImpl.ParameterPsiImpl implements PsiNameIdentifierOwner {

    public ParameterPsiIdOwner(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String getName() {
        return DeclarationPsiNameIdOwner.getElementText(getNameIdentifier());
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(CeylonPsi.IdentifierPsi.class);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        DeclarationPsiNameIdOwner.setElementName(name, getNameIdentifier(), getProject());
        return this;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? super.getTextOffset() : id.getTextOffset();
    }

}
