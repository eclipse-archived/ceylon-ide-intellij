/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.ui {
    DialogWrapper,
    ValidationInfo
}

import java.awt {
    Component
}
import java.io {
    File
}

import org.eclipse.ceylon.ide.intellij {
    CeylonBundle
}

shared class AetherRepositoryDialog(Component parent)
        extends DialogWrapper(parent, false) {

    value form = CeylonAetherRepositoryForm();

    init();
    title = CeylonBundle.message("project.wizard.repo.maven.title");

    createCenterPanel() => form.mainPanel;

    preferredFocusedComponent => form.repoField;

    shared String repository => form.repoField.text;

    shared actual ValidationInfo? doValidate() {
        if (!repository.empty
            && !File(repository).\iexists()) {
            return ValidationInfo(CeylonBundle.message("project.wizard.repo.maven.invalidpath"), form.repoField);
        }
        return null;
    }

}
