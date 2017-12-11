/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.listeners {
    RefactoringElementListener,
    RefactoringElementListenerProvider
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}
import org.eclipse.ceylon.ide.intellij.psi.impl {
    DeclarationPsiNameIdOwner
}

shared class CeylonRefactoringListener() satisfies RefactoringElementListenerProvider {

    shared actual RefactoringElementListener? getListener(PsiElement element) {
        if (exists file = element.containingFile,
            is DeclarationPsiNameIdOwner element) {
            return object satisfies RefactoringElementListener {
                shared actual void elementMoved(PsiElement newElement) {}
                shared actual void elementRenamed(PsiElement newElement) {
                    if (is CeylonFile file) {
                        object extends WriteCommandAction<Nothing>(file.project) {
                            run(Result<Nothing> result) => file.forceReparse();
                        }
                        .execute();
                    }
                }
            };
        }
        return null;
    }
}
