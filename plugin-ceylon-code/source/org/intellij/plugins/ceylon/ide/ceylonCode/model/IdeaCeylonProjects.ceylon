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
    VfsUtilCore,
    LocalFileSystem {
        localFS=instance
    }
}
import com.redhat.ceylon.ide.common.model {
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
    
    newNativeProject(IdeaModule ideArtifact) => IdeaCeylonProject(ideArtifact, this);

    shared actual class VirtualFileSystem() extends super.VirtualFileSystem() {
        
        createVirtualFile(VirtualFile file, IdeaModule project)
                => VirtualFileVirtualFile(file, project);
        
        createVirtualFileFromProject(IdeaModule project, Path path) 
                => VirtualFileVirtualFile(localFS.findFileByIoFile(path.file), project);
                    
        createVirtualFolder(VirtualFile folder, IdeaModule project)
                => IdeaVirtualFolder(folder, project);
        
        createVirtualFolderFromProject(IdeaModule project, Path path) 
                => IdeaVirtualFolder(localFS.findFileByIoFile(path.file), project);
        
        existsOnDisk(VirtualFile resource) => resource.\iexists();
        
        findFile(VirtualFile resource, String fileName) => resource.findChild(fileName);
        
        getParent(VirtualFile resource) => resource.parent;
        
        isFolder(VirtualFile resource) => resource.directory;
        
        toPackageName(VirtualFile resource, VirtualFile sourceDir)
                => VfsUtilCore.getRelativePath(resource, sourceDir)
                    .split(VfsUtilCore.\iVFS_SEPARATOR_CHAR.equals).sequence();
        
        getShortName(VirtualFile resource) => resource.name;
    }
    
    vfs => lazyVfs else (lazyVfs = VirtualFileSystem());

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