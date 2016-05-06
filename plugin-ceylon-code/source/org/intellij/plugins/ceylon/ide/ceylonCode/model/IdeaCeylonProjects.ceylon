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
    CeylonProjects,
    ModelListenerAdapter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    ideaPlatformServices
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
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
}