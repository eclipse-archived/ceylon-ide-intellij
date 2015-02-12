import com.intellij.openapi.components {
    ProjectComponent
}
import com.redhat.ceylon.ide.common {
    CeylonProject,
    CeylonProjects
}
import com.intellij.openapi.\imodule {
    IdeaModule = Module
}
import com.intellij.openapi.project {
    IdeaProject = Project
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
    }
}