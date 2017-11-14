/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.ide.highlighter {
    ArchiveFileType
}
import com.intellij.openapi.fileTypes {
    FileTypeConsumer,
    FileTypeFactory
}

import org.eclipse.ceylon.ide.intellij.lang {
    CeylonFileType
}

shared class CeylonFileTypeFactory() extends FileTypeFactory() {

    shared actual void createFileTypes(FileTypeConsumer consumer) {
        consumer.consume(CeylonFileType.instance, CeylonFileType.instance.defaultExtension);
        consumer.consume(ArchiveFileType.instance, "src;car");
    }

}
