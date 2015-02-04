import com.intellij.openapi.components {
    ProjectComponent
}
import com.redhat.ceylon.ide.common {
    CeylonProject,
    CeylonModel
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}

shared class IdeaCeylonModel(Project ideProject) 
        extends CeylonModel<Module>() 
        satisfies ProjectComponent {
    shared actual CeylonProject<Module> newIdeArtifact(Module ideArtifact)
            => IdeaCeylonProject(ideArtifact);

    shared actual String componentName => "CeylonProjectModel";
    
    shared actual void disposeComponent() {
        print("disposeComponent");
    }
    
    shared actual void initComponent() {
        print("initComponent");
    }
    
    shared actual void projectClosed() {
        print("projectClosed");
    }
    
    shared actual void projectOpened() {
        print("projectOpened");
    }
}