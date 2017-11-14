/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.platform {
    ModelServices,
    PlatformServices,
    VfsServices,
    CommonDocument,
    NoopLinkedMode,
    JavaModelServices
}
import org.eclipse.ceylon.ide.common.util {
    unsafeCast
}
import org.eclipse.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.ceylon.ide.intellij.completion {
    ideaCompletionServices
}

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    shared actual JavaModelServices<JavaClassRoot> 
            javaModel<JavaClassRoot>()
            => unsafeCast<JavaModelServices<JavaClassRoot>>(ideaJavaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaVfsServices);

    shared actual IdeaDocument? gotoLocation(Unit unit, Integer offset, Integer length) {
        return null;
    }
    
    document => ideaDocumentServices;
    completion => ideaCompletionServices;
    
    createLinkedMode(CommonDocument document)
            => if (is IdeaDocument document)
               then IdeaLinkedMode(document)
               else NoopLinkedMode(document);
}
