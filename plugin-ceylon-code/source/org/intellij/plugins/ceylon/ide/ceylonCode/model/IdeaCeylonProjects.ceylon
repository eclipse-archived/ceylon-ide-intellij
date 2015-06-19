import com.intellij.openapi.components {
    ProjectComponent
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    CeylonProjects
}
import com.intellij.openapi.\imodule {
    IdeaModule = Module
}
import com.intellij.openapi.project {
    IdeaProject = Project
}
import com.intellij.compiler {
    CompilerConfiguration
}

shared class IdeaCeylonProjects(IdeaProject ideProject)
        extends CeylonProjects<IdeaModule>()
        satisfies ProjectComponent {
    shared actual CeylonProject<IdeaModule> newIdeArtifact(IdeaModule ideArtifact)
            => IdeaCeylonProject(ideArtifact);

    shared actual String componentName => "CeylonProjects";

    shared actual void disposeComponent() {
        print("disposeComponent");
        clearProjects();
    }

    shared actual void initComponent() {
        print("initComponent");
    }

    shared actual void projectClosed() {
        print("projectClosed");
        clearProjects();
    }

    shared actual void projectOpened() {
        print("projectOpened");

        // Do not treat .ceylon files as resources, otherwise they are copied in the output directory during compilation
        value compilerConfiguration = CompilerConfiguration.getInstance(ideProject);
        if (compilerConfiguration.isResourceFile("lol.ceylon")) {
            compilerConfiguration.addResourceFilePattern("!?*.ceylon");
        }
    }
}