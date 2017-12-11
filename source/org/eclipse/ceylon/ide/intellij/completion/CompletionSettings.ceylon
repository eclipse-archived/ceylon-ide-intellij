/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.components {
    ...
}
import org.eclipse.ceylon.ide.common.settings {
    CompletionOptions
}

state__TYPE {
    name = "CeylonCompletionSettings";
    storages = {
        storage {
            file = "$APP_CONFIG$/ceylon.xml";
        }
    };
}
shared class CompletionSettings() satisfies PersistentStateComponent<CompletionOptions> {

    state => completionOptions;

    loadState(CompletionOptions state) => completionOptions = state;

    shared CompletionOptions options => completionOptions;
}

shared CompletionSettings completionSettings
        => ApplicationManager.application.isDisposed()
        then CompletionSettings()
        else ServiceManager.getService(`CompletionSettings`);