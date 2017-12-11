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
    AnActionEvent
}

import org.eclipse.ceylon.ide.intellij.settings {
    ceylonSettings
}

shared class LowerModelUpdatePriorityAction() extends AnAction() {

    shared actual void actionPerformed(AnActionEvent e) {
        ceylonSettings.lowerModelUpdatePriority
        = !ceylonSettings.lowerModelUpdatePriority;
    }

    shared actual void update(AnActionEvent e) {
        e.presentation.enabled = true;

        e.presentation.text =
        if (ceylonSettings.lowerModelUpdatePriority)
        then "Restore the processing priority of Ceylon model updates to the defaut priority"
        else "Decrease the processing priority of Ceylon model updates";
    }
}
