import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile,
    LocalFileSystem {
        localFS=instance
    },
    VfsUtilCore
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    VfsServices
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import com.redhat.ceylon.ide.common.vfs {
    FolderVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.lang.ref {
    WeakReference
}

import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    vfsKeychain,
    IdeaVirtualFolder,
    VirtualFileVirtualFile
}

object ideaVfsServices satisfies VfsServices<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    createVirtualFile(VirtualFile file, Module project) => VirtualFileVirtualFile(file, project);
    
    createVirtualFileFromProject(Module project, Path path) => VirtualFileVirtualFile(localFS.findFileByIoFile(path.file), project);
    
    createVirtualFolder(VirtualFile folder, Module project) => IdeaVirtualFolder(folder, project);
    
    createVirtualFolderFromProject(Module project, Path path) => IdeaVirtualFolder(localFS.findFileByIoFile(path.file), project);
    
    existsOnDisk(VirtualFile resource) => resource.\iexists();
    
    findFile(VirtualFile resource, String fileName) => resource.findChild(fileName);
    
    getPackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder)
            => WeakReference(folder.getUserData(vfsKeychain.findOrCreate<Package>(ceylonProject.ideArtifact)));
    
    getParent(VirtualFile resource) => resource.parent;
    
    shared actual Path getPath(VirtualFile resource) => Path(getPathString(resource));
    
    shared actual String getPathString(VirtualFile resource) => resource.canonicalPath;
    
    getRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder) 
        => rootFolder.getUserData(vfsKeychain.findOrCreate<Boolean>(ceylonProject.ideArtifact));
    
    getRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder)
            => WeakReference<FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>>(folder.getUserData(vfsKeychain.findOrCreate<IdeaVirtualFolder>(ceylonProject.ideArtifact)));
    
    getShortName(VirtualFile resource) => resource.name;
    
    isFolder(VirtualFile resource) => resource.directory;
    
    setPackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<Package> p) 
        => folder.putUserData(vfsKeychain.findOrCreate<Package>(ceylonProject.ideArtifact), p.get());
    
    shared actual void setRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder, Boolean isSource) 
        => rootFolder.putUserData(vfsKeychain.findOrCreate<Boolean>(ceylonProject.ideArtifact), isSource);
    
    shared actual void setRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>> root) {
        assert(is IdeaVirtualFolder r = root.get());
        folder.putUserData(vfsKeychain.findOrCreate<IdeaVirtualFolder>(ceylonProject.ideArtifact), r);
    }
    
    toPackageName(VirtualFile resource, VirtualFile sourceDir) 
        => VfsUtilCore.getRelativePath(resource, sourceDir)
            .split(VfsUtilCore.\iVFS_SEPARATOR_CHAR.equals).sequence();
}
