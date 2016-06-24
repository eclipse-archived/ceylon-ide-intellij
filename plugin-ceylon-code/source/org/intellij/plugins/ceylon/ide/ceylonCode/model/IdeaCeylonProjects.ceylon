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
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    ideaPlatformServices
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaCeylonProjects(shared IdeaProject ideaProject)
        extends CeylonProjects<IdeaModule,VirtualFile,VirtualFile,VirtualFile>()
        satisfies ProjectComponent {
    ideaPlatformServices.register();
    
    object ceylonProjectCleaner satisfies ModelListenerAdapter<IdeaModule,VirtualFile,VirtualFile,VirtualFile> {
        ceylonProjectRemoved(CeylonProjectAlias ceylonProject) =>
                unsafeCast<IdeaCeylonProject>(ceylonProject).clean();
    }
    
    newNativeProject(IdeaModule ideArtifact) => IdeaCeylonProject(ideArtifact, this);

    componentName => "CeylonProjects";

    initComponent() => addModelListener(ceylonProjectCleaner);
    disposeComponent() => removeModelListener(ceylonProjectCleaner);


    shared actual void projectClosed() {
        clearProjects();
    }

    shared actual void projectOpened() {

        // Do not treat .ceylon files as resources, otherwise they are copied in the output directory during compilation
        value compilerConfiguration = CompilerConfiguration.getInstance(ideaProject);
        if (compilerConfiguration.isResourceFile("lol.ceylon")) {
            compilerConfiguration.addResourceFilePattern("!?*.ceylon");
        }
    }

    shared ExternalPhasedUnit? findExternalPhasedUnit(CeylonFile|VirtualFile file) {
        value path = if (is CeylonFile file) then file.virtualFile?.path else file.path;

        if (exists path,
            exists offset = path.firstInclusion(JarFileSystem.jarSeparator)) {

            value start = path.startsWith(JarFileSystem.protocolPrefix)
                          then JarFileSystem.protocolPrefix.size
                          else 0;
            value archivePath = path.span(start, offset - 1);
            value filePath = path.spanFrom(offset + JarFileSystem.jarSeparator.size);

            return expand {
                ceylonProjects*.modules.coalesced*.external*.find(
                    (m) => if (exists sap=m.sourceArchivePath) then sap == archivePath else false
                )
            }.coalesced.map(
                (m) => m.getPhasedUnitFromRelativePath(filePath)
            ).coalesced.first;
        }

        return null;
    }
}