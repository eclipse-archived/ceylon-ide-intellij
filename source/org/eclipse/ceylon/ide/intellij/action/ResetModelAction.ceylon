/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.actionSystem {
    AnAction,
    AnActionEvent,
    CommonDataKeys
}

import org.eclipse.ceylon.ide.intellij.model {
    getCeylonProjects,
    getModelManager
}

shared class ResetModelAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project)) {

            for (p in projects.ceylonProjects) {
                p.build.requestFullBuild();
                p.build.classPathChanged();
            }

            if (exists man = getModelManager(project)) {
                man.scheduleModelUpdate(0, true);
            }
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists project = CommonDataKeys.project.getData(e.dataContext),
            exists projects = getCeylonProjects(project),
            !projects.ceylonProjects.empty) {

            e.presentation.enabled = true;
        } else {
            e.presentation.enabled = false;
        }
    }
}
