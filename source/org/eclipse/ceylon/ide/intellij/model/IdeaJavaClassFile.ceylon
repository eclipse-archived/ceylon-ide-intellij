/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import org.eclipse.ceylon.ide.common.model {
    JavaClassFile
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}

shared class IdeaJavaClassFile(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    LazyPackage pkg)
        extends JavaClassFile<Module,VirtualFile,VirtualFile,PsiClass,PsiClass|PsiMethod>
        (cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    javaClassRootToNativeFile(PsiClass javaClassRoot)
        => javaClassRoot.containingFile.virtualFile;
    
    javaClassRootToNativeRootFolder(PsiClass javaClassRoot)
        => javaClassRoot.containingFile.virtualFile.parent;
}
