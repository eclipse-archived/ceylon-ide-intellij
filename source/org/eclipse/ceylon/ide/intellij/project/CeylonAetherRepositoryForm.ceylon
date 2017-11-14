/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.fileChooser {
    FileChooserDescriptorFactory
}
import com.intellij.ide.highlighter {
    XmlFileType
}
import org.eclipse.ceylon.ide.intellij {
    CeylonBundle
}

shared class CeylonAetherRepositoryForm()
        extends AetherRepositoryForm() {

    value descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(XmlFileType.instance)
        .withFileFilter((virtualFile) => virtualFile.name.endsWith("settings.xml"));

    repoField.addBrowseFolderListener(CeylonBundle.message("project.wizard.repo.maven.selectpath"),
                                      null, null, descriptor);

}