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
    LangDataKeys
}
import org.eclipse.ceylon.ide.intellij.util {
    icons
}

shared class ConfigureCeylonAction() extends AnAction(icons.ceylon) {
    shared actual void actionPerformed(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.moduleContext)) {
            AndroidStudioSupportImpl().setupModule(mod);
        }
    }

    shared actual void update(AnActionEvent e) {
        if (exists mod = e.getData(LangDataKeys.moduleContext)) {
            e.presentation.enabledAndVisible = true;
        } else {
            e.presentation.enabledAndVisible = false;
        }
    }
}
