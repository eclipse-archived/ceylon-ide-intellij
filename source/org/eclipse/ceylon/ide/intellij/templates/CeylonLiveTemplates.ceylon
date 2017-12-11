/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight.template {
    FileTypeBasedContextType
}
import com.intellij.codeInsight.template.impl {
    DefaultLiveTemplatesProvider
}

import java.lang {
    ObjectArray,
    Types
}

import org.eclipse.ceylon.ide.intellij.lang {
    CeylonFileType
}

shared class CeylonLiveTemplateContext()
        extends FileTypeBasedContextType("CEYLON", "&Ceylon", CeylonFileType.instance) {}


shared class CeylonLiveTemplatesProvider()
        satisfies DefaultLiveTemplatesProvider {

    value files = ["/liveTemplates/CeylonLiveTemplates"];

    defaultLiveTemplateFiles = ObjectArray.with(files.map(Types.nativeString));

    hiddenLiveTemplateFiles => null;

}
