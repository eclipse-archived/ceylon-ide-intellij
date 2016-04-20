import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile,
    LocalFileSystem {
        localFS=instance
    },
    VfsUtilCore,
    VfsUtil
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
import java.io {
    File
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
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

    findChild(VirtualFile parent, Path path) => parent.findChild(path.string);

    fromJavaFile(File javaFile, Module project) => VfsUtil.findFileByIoFile(javaFile, true);

    // TODO check if it's prefixed with a protocol
    getJavaFile(VirtualFile resource) => File(resource.canonicalPath);

    getVirtualFilePath(VirtualFile resource) => Path(getVirtualFilePathString(resource));

    getVirtualFilePathString(VirtualFile resource) => resource.canonicalPath;

    getProjectRelativePath(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> project)
        => if (exists path = getProjectRelativePathString(resource, project))
           then Path(path)
           else null;

    getProjectRelativePathString(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> project)
        => if (is IdeaCeylonProject project)
           then VfsUtil.getRelativePath(resource, project.moduleRoot)
           else null;

    shared actual void removePackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        if (exists key = vfsKeychain.find<Package>(ceylonProject.ideArtifact)) {
            folder.putUserData(key, null);
        }
    }

    shared actual void removeRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        if (exists key = vfsKeychain.find<Boolean>(ceylonProject.ideArtifact)) {
            folder.putUserData(key, null);
        }
    }

    shared actual void removeRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        if (exists key = vfsKeychain.find<IdeaVirtualFolder>(ceylonProject.ideArtifact)) {
            folder.putUserData(key, null);
        }
    }
}
