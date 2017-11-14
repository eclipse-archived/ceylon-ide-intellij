/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight.editorActions.moveLeftRight {
    MoveElementLeftRightHandler
}
import com.intellij.psi {
    PsiElement
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonPsi
}

shared class CeylonMoveLeftRightHandler() extends MoveElementLeftRightHandler() {

    getMovableSubElements(PsiElement element)
            => if (element is CeylonPsi.PositionalArgumentListPsi
                            | CeylonPsi.SequencedArgumentPsi
                            | CeylonPsi.ParameterListPsi
                            | CeylonPsi.ImportMemberOrTypeListPsi)
            then element.children
            else PsiElement.emptyArray;
}
