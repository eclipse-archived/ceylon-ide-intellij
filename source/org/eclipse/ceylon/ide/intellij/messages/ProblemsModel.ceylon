/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import ceylon.collection {
    HashMap
}

import org.eclipse.ceylon.ide.common.model {
    Severity
}

import org.eclipse.ceylon.ide.intellij.model {
    IdeaCeylonProject
}

class ProblemsModel() {
    value problemsByProject = HashMap<IdeaCeylonProject, Problems>();

    class Problems(project, frontendMessages, backendMessages, projectMessages) {

        IdeaCeylonProject project;

        {SourceMsg*}? frontendMessages;
        {SourceMsg*}? backendMessages;
        {ProjectMsg*}? projectMessages;

        shared Integer count(Severity s)
            => [frontendMessages, backendMessages, projectMessages].coalesced.fold(0,
                (initial, messages)
                        => initial + messages.count((msg) => msg.severity == s));

        shared Integer countWarnings() => count(Severity.warning);

        shared Integer countErrors() => count(Severity.error);

        shared {BuildMsg*} allMessages
                => expand([frontendMessages, backendMessages, projectMessages].coalesced);
    }

    shared void updateProblems(IdeaCeylonProject project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages) {

        problemsByProject[project]
            = Problems {
                project = project;
                frontendMessages = frontendMessages;
                backendMessages = backendMessages;
                projectMessages = projectMessages;
            };
    }

    shared Integer count(Severity s)
            => problemsByProject.items.fold(0, (sum, item) => sum + item.count(s));

    shared Integer countWarnings()
            => problemsByProject.items.fold(0, (sum, item) => sum + item.countWarnings());

    shared Integer countErrors()
            => problemsByProject.items.fold(0, (sum, item) => sum + item.countErrors());

    shared {BuildMsg*} allMessages => expand(problemsByProject.items.map(Problems.allMessages));

    shared void clear() {
        problemsByProject.clear();
    }
}
