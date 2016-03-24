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
    VirtualFile
}
import com.redhat.ceylon.ide.common.model {
    CeylonProjects
}

shared class IdeaCeylonProjects(IdeaProject ideProject)
        extends CeylonProjects<IdeaModule,VirtualFile,VirtualFile,VirtualFile>()
        satisfies ProjectComponent {
    
    newNativeProject(IdeaModule ideArtifact) => IdeaCeylonProject(ideArtifact, this);

    componentName => "CeylonProjects";

    shared actual void disposeComponent() {
        ceylonProjects.each((p) {
            assert(is IdeaCeylonProject p);
            p.shutdownFileWatcher();
        });
        clearProjects();
    }

    shared actual void initComponent() {
    }

    shared actual void projectClosed() {
        clearProjects();
    }

    shared actual void projectOpened() {

        // Do not treat .ceylon files as resources, otherwise they are copied in the output directory during compilation
        value compilerConfiguration = CompilerConfiguration.getInstance(ideProject);
        if (compilerConfiguration.isResourceFile("lol.ceylon")) {
            compilerConfiguration.addResourceFilePattern("!?*.ceylon");
        }
    }
}