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
import com.redhat.ceylon.ide.common.model {
    CeylonProjects,
    ModelListenerAdapter
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    ideaPlatformServices
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.codeInsight.intention {
    IntentionManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    AbstractIntention
}
import org.intellij.plugins.ceylon.ide.ceylonCode.messages {
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

    shared [String, String]? parseExternalPhasedUnitFullPath(<CeylonFile|VirtualFile>? file) {
        value path = if (is CeylonFile? file) then file?.virtualFile?.path else file.path;
        
        if (exists path,
            exists offset = path.firstInclusion(JarFileSystem.jarSeparator)) {
            
            value start
                    = path.startsWith(JarFileSystem.protocolPrefix)
                    then JarFileSystem.protocolPrefix.size
                    else 0;
            value archivePath = path.span(start, offset - 1);
            value filePath = path.spanFrom(offset + JarFileSystem.jarSeparator.size);
            return [archivePath, filePath];
        }
        return null;
    }

    IdeModuleAlias? findModuleForParsedArchivePaths([String, String]? parsedArchivePaths) {
        if (exists parsedArchivePaths) {
            return expand(ceylonProjects*.modules.coalesced*.external)
                    .filter((m) => if (exists sap=m.sourceArchivePath) then sap == parsedArchivePaths[0] else false)
                    .find((m) => m.containsPhasedUnitWithRelativePath(parsedArchivePaths[1]));
        }
        return null;
    }

    shared IdeModuleAlias? findModuleForExternalPhasedUnit(<CeylonFile|VirtualFile>? file)
            => findModuleForParsedArchivePaths(parseExternalPhasedUnitFullPath(file));

    shared ExternalPhasedUnit? findExternalPhasedUnit(<CeylonFile|VirtualFile>? file)
            => let (parsedArchivePaths = parseExternalPhasedUnitFullPath(file))
            if (exists parsedArchivePaths)
            then findModuleForParsedArchivePaths(parsedArchivePaths)
                    ?.getPhasedUnitFromRelativePath(parsedArchivePaths[1])
            else null;
}