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
    VfsUtilCore
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    CeylonProjects
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    VirtualFileVirtualFile,
    IdeaVirtualFolder
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
        
        shared actual FileVirtualFileAlias createVirtualFile(VirtualFile file,
            IdeaModule project) {
            
            return VirtualFileVirtualFile(file, project);
        }
        
        shared actual FileVirtualFileAlias createVirtualFileFromProject
        (IdeaModule project, Path path) => nothing;
        
        shared actual FolderVirtualFileAlias createVirtualFolder(VirtualFile folder,
            IdeaModule project) {
            
            return IdeaVirtualFolder(folder, project);
        }
        
        shared actual FolderVirtualFileAlias createVirtualFolderFromProject
        (IdeaModule project, Path path) => nothing;
        
        shared actual Boolean existsOnDisk(VirtualFile resource)
                => resource.\iexists();
        
        shared actual VirtualFile? findFile(VirtualFile resource, String fileName)
                => resource.findChild(fileName);
        
        shared actual VirtualFile? getParent(VirtualFile resource)
                => resource.parent;
        
        shared actual Boolean isFolder(VirtualFile resource)
                => resource.directory;
        
        shared actual String[] toPackageName(VirtualFile resource,
                                             VirtualFile sourceDir)
                => VfsUtilCore.getRelativePath(resource, sourceDir)
                    .split(VfsUtilCore.\iVFS_SEPARATOR_CHAR.equals).sequence();
        
        shared actual String getShortName(VirtualFile resource) => resource.name;
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