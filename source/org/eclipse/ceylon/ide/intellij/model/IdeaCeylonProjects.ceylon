/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.compiler {
    CompilerConfiguration
}
import com.intellij.openapi.components {
    ProjectComponent
}
import com.intellij.openapi.\imodule {
    IdeaModule=Module
}
import com.intellij.openapi.project {
    IdeaProject=Project
}
import com.intellij.openapi.vfs {
    VirtualFile,
    JarFileSystem
}
import org.eclipse.ceylon.ide.common.model {
    CeylonProjects,
    ModelListenerAdapter
}
import org.eclipse.ceylon.ide.common.typechecker {
    ExternalPhasedUnit
}

import org.eclipse.ceylon.ide.intellij.platform {
    ideaPlatformServices
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}
import com.intellij.codeInsight.intention {
    IntentionManager
}
import org.eclipse.ceylon.ide.intellij.correct {
    AbstractIntention
}
import org.eclipse.ceylon.ide.intellij.messages {
    BackendMessageHandler
}
import com.intellij.compiler.server {
    BuildManagerListener,
    CustomBuilderMessageHandler
}
import com.intellij.util.messages {
    MessageBusConnection
}

shared class IdeaCeylonProjects(shared IdeaProject ideaProject)
        extends CeylonProjects<IdeaModule,VirtualFile,VirtualFile,VirtualFile>()
        satisfies ProjectComponent {

    ideaPlatformServices.register();
    
    object ceylonProjectCleaner satisfies ModelListenerAdapter<IdeaModule,VirtualFile,VirtualFile,VirtualFile> {
        shared actual void ceylonProjectRemoved(CeylonProjectAlias ceylonProject) {
            assert (is IdeaCeylonProject ceylonProject);
            ceylonProject.clean();
        }
    }

    value buildListener = BackendMessageHandler(ideaProject);
    late MessageBusConnection busConnection;

    newNativeProject(IdeaModule ideArtifact) => IdeaCeylonProject(ideArtifact, this);

    componentName => "CeylonProjects";

    shared actual void initComponent() {
        addModelListener(ceylonProjectCleaner);
        busConnection = ideaProject.messageBus.connect();
        busConnection.subscribe(BuildManagerListener.topic, buildListener);
        busConnection.subscribe(CustomBuilderMessageHandler.topic, buildListener);
    }

    shared actual void disposeComponent() {
        removeModelListener(ceylonProjectCleaner);
        busConnection.disconnect();
    }


    shared actual void projectClosed() {
        clearProjects();
        for (intention in IntentionManager.instance.intentionActions) {
            if (is AbstractIntention intention) {
                intention.clear();
            }
        }
    }

    shared actual void projectOpened() {

        // Do not treat .ceylon files as resources, otherwise they are copied in the output directory during compilation
        value compilerConfiguration = CompilerConfiguration.getInstance(ideaProject);
        if (compilerConfiguration.isResourceFile("lol.ceylon")) {
            compilerConfiguration.addResourceFilePattern("!?*.ceylon");
        }
    }

    shared String[2]? parseExternalPhasedUnitFullPath(<CeylonFile|VirtualFile>? file) {
        if (exists file,
            exists path = if (is CeylonFile file) then file.virtualFile?.path else file.path,
            exists offset = path.firstInclusion(JarFileSystem.jarSeparator)) {
            value start
                    = path.startsWith(JarFileSystem.protocolPrefix)
                    then JarFileSystem.protocolPrefix.size
                    else 0;
            value archivePath = path[start..offset-1];
            value filePath = path[offset + JarFileSystem.jarSeparator.size...];
            return [archivePath, filePath];
        }
        else {
            return null;
        }
    }

    IdeModuleAlias? findModuleForParsedArchivePaths(String[2]? parsedArchivePaths)
            => if (exists [first, second] = parsedArchivePaths)
            then
                { for (project in ceylonProjects)
                  if (exists modules = project.modules)
                  for (mod in modules.external)
                  if (exists sap = mod.sourceArchivePath, sap == first,
                      mod.containsPhasedUnitWithRelativePath(second))
                  mod }.first
            else null;

    shared IdeModuleAlias? findModuleForExternalPhasedUnit(<CeylonFile|VirtualFile>? file)
            => findModuleForParsedArchivePaths(parseExternalPhasedUnitFullPath(file));

    shared ExternalPhasedUnit? findExternalPhasedUnit(<CeylonFile|VirtualFile>? file)
            => let (parsedArchivePaths = parseExternalPhasedUnitFullPath(file))
            if (exists parsedArchivePaths)
            then findModuleForParsedArchivePaths(parsedArchivePaths)
                    ?.getPhasedUnitFromRelativePath(parsedArchivePaths[1])
            else null;
}