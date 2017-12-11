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
import org.eclipse.ceylon.ide.common.model {
    BaseIdeModule,
    IdeModuleSourceMapper
}
import org.eclipse.ceylon.compiler.typechecker.context {
    Context
}
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status
}

shared class IdeaModuleSourceMapper(
    Context context, IdeaModuleManager moduleManager)
        extends IdeModuleSourceMapper
        <Module,VirtualFile,VirtualFile,VirtualFile>(context, moduleManager) {
    
    shared actual String defaultCharset
            => moduleManager.ceylonProject?.defaultCharset else "UTF-8";
    
    shared actual void logModuleResolvingError(BaseIdeModule mod, Exception e) {
        platformUtils.log(Status._ERROR,
            "Failed to resolve module " + mod.signature, e);
    }
}
