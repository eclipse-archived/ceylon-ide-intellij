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
    PsiMethod,
    PsiClass
}
import org.eclipse.ceylon.ide.common.model {
    CrossProjectJavaCompilationUnit,
    BaseCeylonProject
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}

shared class IdeaCrossProjectJavaCompilationUnit(
    BaseCeylonProject ceylonProject,
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    LazyPackage pkg)
        extends CrossProjectJavaCompilationUnit<Module,VirtualFile, VirtualFile,PsiClass,PsiClass|PsiMethod>
        (ceylonProject, cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    shared actual VirtualFile javaClassRootToNativeFile(PsiClass cls)
            => cls.containingFile.virtualFile;
    
    shared actual VirtualFile javaClassRootToNativeRootFolder(PsiClass cls)
            => cls.containingFile.virtualFile.parent;
}
