/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.integrations.maven;

import com.intellij.util.PairConsumer;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

/**
 * Workaround for https://github.com/ceylon/ceylon/issues/6829.
 */
public abstract class WorkaroundForIssue6829 extends MavenImporter {
    public WorkaroundForIssue6829(String pluginGroupID, String pluginArtifactID) {
        super(pluginGroupID, pluginArtifactID);
    }

    @Override
    public void collectSourceRoots(MavenProject mavenProject,
                                   PairConsumer<String, JpsModuleSourceRootType<?>> result) {
        myCollectSourceRoots(mavenProject, result);
    }

    public abstract void myCollectSourceRoots(MavenProject mavenProject,
                                            PairConsumer<String, JpsModuleSourceRootType<? extends JpsElement>> result);
}
