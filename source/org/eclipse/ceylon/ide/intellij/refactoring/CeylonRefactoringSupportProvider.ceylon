/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.lang.refactoring {
    RefactoringSupportProvider
}
import com.intellij.psi {
    PsiElement
}

import org.eclipse.ceylon.ide.intellij.refactoring {
    CeylonChangeSignatureHandler,
    ExtractFunctionHandler,
    ExtractValueHandler
}

shared class CeylonRefactoringSupportProvider() extends RefactoringSupportProvider() {

    isInplaceRenameAvailable(PsiElement element, PsiElement context)
            => false; // CeylonVariableRenameHandler handles this

    changeSignatureHandler => CeylonChangeSignatureHandler();

    introduceVariableHandler => ExtractValueHandler();

    extractMethodHandler => ExtractFunctionHandler();
}
