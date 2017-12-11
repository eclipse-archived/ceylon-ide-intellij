/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.refactoring.copy {
    CopyHandlerDelegateBase,
    CopyFilesOrDirectoriesHandler
}
import com.intellij.psi {
    PsiElement,
    PsiDirectory
}
import java.lang {
    ObjectArray
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

"By default, IntelliJ tries to find `PsiClass`es to rename in the
 copy refactoring. This class makes sure it's also possible to
 rename Ceylon *files* that don't contain `PsiClass`es when they
 are copied."
shared class CopyCeylonFileHandler() extends CopyHandlerDelegateBase() {

    value delegate = CopyFilesOrDirectoriesHandler();

    function adjustElements(ObjectArray<PsiElement> elements) {
        return ObjectArray<PsiElement>.with {
            for (el in elements)
            if (el.valid, is CeylonFile file = el.containingFile)
            then file
            else el
        };
    }

    canCopy(ObjectArray<PsiElement> elements, Boolean fromUpdate)
            => delegate.canCopy(adjustElements(elements), fromUpdate);

    doClone(PsiElement? element)
            => delegate.doClone(element);

    doCopy(ObjectArray<PsiElement> elements, PsiDirectory? defaultTargetDirectory)
            => delegate.doCopy(adjustElements(elements), defaultTargetDirectory);
}
