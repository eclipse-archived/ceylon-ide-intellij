/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.ide.structureView {
    TreeBasedStructureViewBuilder
}
import com.intellij.lang {
    PsiStructureViewFactory
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.psi {
    PsiFile
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class CeylonStructureViewFactory()
        satisfies PsiStructureViewFactory {

    getStructureViewBuilder(PsiFile psiFile)
            => if (is CeylonFile psiFile)
            then object extends TreeBasedStructureViewBuilder() {
                createStructureViewModel(Editor editor)
                        => CeylonFileTreeModel(psiFile);
                rootNodeShown => false;
            }
            else null;
}
