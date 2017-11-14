/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight {
    TargetElementEvaluatorEx2
}
import com.intellij.psi {
    PsiElement,
    PsiReference
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonPsi
}

shared class CeylonTargetElementEvaluator() extends TargetElementEvaluatorEx2() {

    getElementByReference(PsiReference ref, Integer flags) => null;

    getNamedElement(PsiElement element)
            => if (is CeylonPsi.IdentifierPsi id = element.parent,
                   is CeylonPsi.DeclarationPsi dec = id.parent)
                        then dec else null;
}
