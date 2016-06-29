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

import java.io {
    File
}
import java.lang.ref {
    WeakReference
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    IdeaVirtualFolder,
    VirtualFileVirtualFile
}

object ideaVfsServices satisfies VfsServices<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    function localFile(File file) {
        assert(exists f = localFS.findFileByIoFile(file));
        return f;
    }

    createVirtualFile(VirtualFile file, Module project) => VirtualFileVirtualFile(file, project);
    
    createVirtualFileFromProject(Module project, Path path) => VirtualFileVirtualFile(localFile(path.file), project);
    
    createVirtualFolder(VirtualFile folder, Module project) => IdeaVirtualFolder(folder, project);
    
    createVirtualFolderFromProject(Module project, Path path) => IdeaVirtualFolder(localFile(path.file), project);
    
    existsOnDisk(VirtualFile resource) => resource.\iexists();
    
    findFile(VirtualFile resource, String fileName) => resource.findChild(fileName);
    
    IdeaCeylonProject.\InativeFolderProperties nativeFolderProperties(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject) {
        assert(is IdeaCeylonProject ceylonProject);
        return ceylonProject.nativeFolderProperties;
    }
    
    getPackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder)
            => WeakReference(folder.getUserData(nativeFolderProperties(ceylonProject).packageModel));
    
    getParent(VirtualFile resource) => resource.parent;

    getRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder)
        => rootFolder.getUserData(nativeFolderProperties(ceylonProject).rootIsSource);
    
    getRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder)
            => WeakReference<FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>>(folder.getUserData(nativeFolderProperties(ceylonProject).root));
    
    getShortName(VirtualFile resource) => resource.name;
    
    isFolder(VirtualFile resource) => resource.directory;
    
    setPackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<Package> p) => 
            folder.putUserData(nativeFolderProperties(ceylonProject).packageModel, p.get());
    
    setRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder, Boolean isSource) => 
            rootFolder.putUserData(nativeFolderProperties(ceylonProject).rootIsSource, isSource);
    
    shared actual void setRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>> root) {
        assert(is IdeaVirtualFolder r = root.get());
        folder.putUserData(nativeFolderProperties(ceylonProject).root, r);
    }
    
    shared actual String[] toPackageName(VirtualFile resource, VirtualFile sourceDir) {
        return if (exists path = VfsUtilCore.getRelativePath(resource, sourceDir))
        then path.split(VfsUtilCore.vfsSeparatorChar.equals).sequence()
        else [];
    }

    findChild(VirtualFile|Module parent, Path path) => 
            if (is VirtualFile parent)
            then parent.findFileByRelativePath(path.string)
            else parent.moduleFile?.parent?.findFileByRelativePath(path.string);

    fromJavaFile(File javaFile, Module project) => VfsUtil.findFileByIoFile(javaFile, true);

    // TODO check if it's prefixed with a protocol
    getJavaFile(VirtualFile resource) => File(resource.canonicalPath);

    getVirtualFilePath(VirtualFile resource) => Path(getVirtualFilePathString(resource));

    getVirtualFilePathString(VirtualFile resource) => resource.canonicalPath else "<unknown>";

    getProjectRelativePath(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>|Module project)
        => if (exists path = getProjectRelativePathString(resource, project))
           then Path(path)
           else null;

    getProjectRelativePathString(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>|Module project)
        => if (is IdeaCeylonProject project)
           then VfsUtil.getRelativePath(resource, project.moduleRoot)
           else null;

    shared actual void removePackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        folder.putUserData(nativeFolderProperties(ceylonProject).packageModel, null);
    }

    shared actual void removeRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        folder.putUserData(nativeFolderProperties(ceylonProject).rootIsSource, null);
    }

    shared actual void removeRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder) {
        folder.putUserData(nativeFolderProperties(ceylonProject).root, null);
    }
}
