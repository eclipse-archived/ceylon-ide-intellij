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
    CeylonProject,
    CeylonProjects
}
import com.redhat.ceylon.ide.common.util {
    Path
}

shared class IdeaCeylonProjects(IdeaProject ideProject)
        extends CeylonProjects<IdeaModule,VirtualFile,VirtualFile,VirtualFile>()
        satisfies ProjectComponent {
    
    variable VirtualFileSystem? lazyVfs = null;
    
    shared actual CeylonProject<IdeaModule,VirtualFile,VirtualFile,VirtualFile>
            newNativeProject(IdeaModule ideArtifact)
            => IdeaCeylonProject(ideArtifact, this);

    shared actual class VirtualFileSystem()
            extends super.VirtualFileSystem() {
        shared actual FileVirtualFileAlias createVirtualFile(VirtualFile file)
                 => nothing;
        
        shared actual FileVirtualFileAlias createVirtualFileFromProject
        (IdeaModule project, Path path) => nothing;
        
        shared actual FolderVirtualFileAlias createVirtualFolder
        (VirtualFile folder) => nothing;
        
        shared actual FolderVirtualFileAlias createVirtualFolderFromProject
        (IdeaModule project, Path path) => nothing;
        
    }
    
    shared actual VirtualFileSystem vfs 
            => lazyVfs else (lazyVfs = VirtualFileSystem());


    shared actual String componentName => "CeylonProjects";

    shared actual void disposeComponent() {
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