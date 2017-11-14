/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager
}

import org.eclipse.ceylon.ide.intellij.lang {
    ceylonFileType,
    ceylonLanguage
}

shared class CeylonSourceFileViewProvider(
    PsiManager manager,
    VirtualFile virtualFile,
    Boolean eventSystemEnabled) 
        extends SingleRootFileViewProvider(
        manager, 
        virtualFile, 
        eventSystemEnabled, 
        ceylonLanguage,
        ceylonFileType) {
}