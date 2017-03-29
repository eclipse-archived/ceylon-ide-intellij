import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    ProjectManager
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
    VfsServices,
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.util {
    Path
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

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    IdeaCeylonProjects,
    CeylonModelManager
}
import org.intellij.plugins.ceylon.ide.vfs {
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
            => folder.getUserData(nativeFolderProperties(ceylonProject).packageModel);
    
    getParent(VirtualFile resource) => resource.parent;

    getRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder)
        => rootFolder.getUserData(nativeFolderProperties(ceylonProject).rootIsSource);
    
    shared actual WeakReference<FolderVirtualFileAlias>? getRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder)
            => folder.getUserData(nativeFolderProperties(ceylonProject).root);
    
    getShortName(VirtualFile resource) => resource.name;
    
    isFolder(VirtualFile resource) => resource.directory;
    
    setPackagePropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<Package> p) => 
            folder.putUserData(nativeFolderProperties(ceylonProject).packageModel, p);
    
    setRootIsSourceProperty(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile rootFolder, Boolean isSource) => 
            rootFolder.putUserData(nativeFolderProperties(ceylonProject).rootIsSource, isSource);
    
    shared actual void setRootPropertyForNativeFolder(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject, VirtualFile folder, WeakReference<FolderVirtualFileAlias> root) {
        folder.putUserData(nativeFolderProperties(ceylonProject).root, root);
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

    shared actual Boolean flushIfNecessary(VirtualFile resource) {
        value fileDocumentManager = FileDocumentManager.instance;
        if (fileDocumentManager.isFileModified(resource)) {
            if (exists document = fileDocumentManager.getCachedDocument(resource)){
                value application = ApplicationManager.application;
                if (application.dispatchThread) {
                    fileDocumentManager.saveDocument(document);
                    return true;
                } else {
                    // The file could not be flushed to disk synchronously, since we're not
                    // on the UI thread.
                    // So do it asynchronously, and further notify the change to the
                    // model manager in case that should trigger a model update.
                    application.invokeLater(() {
                        fileDocumentManager.saveDocument(document);
                        for (project in ProjectManager.instance.openProjects) {
                            if (exists modelManager = project.getComponent(`CeylonModelManager`)) {
                                modelManager.notifyFileContentChange(resource);
                            }
                        }
                    });
                    return false;
                }
            } else {
                platformUtils.log(Status._WARNING, "Modified file `` resource.path `` was not saved.");
                return false;
            }
        }
        return true;
    }


    // TODO check if it's prefixed with a protocol
    getJavaFile(VirtualFile resource) => File(resource.path);

    getVirtualFilePath(VirtualFile resource) => Path(getVirtualFilePathString(resource));

    getVirtualFilePathString(VirtualFile resource) => resource.path;

    getProjectRelativePath(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>|Module project)
        => if (exists path = getProjectRelativePathString(resource, project))
           then Path(path)
           else null;

    shared actual String? getProjectRelativePathString(VirtualFile resource, CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>|Module project) {
        IdeaCeylonProject ceylonProject;
        switch (project)
        case (is Module) {
            value model = project.project.getComponent(`IdeaCeylonProjects`);
            value existingCeylonProject = model.getProject(project);
            if (is IdeaCeylonProject existingCeylonProject) {
                ceylonProject = existingCeylonProject;
            } else {
                return null;
            }
                    
        }
        else case (is IdeaCeylonProject) {
            ceylonProject = project;
        }
        else {
            return null;
        }
        return VfsUtil.getRelativePath(resource, ceylonProject.moduleRoot);
    }

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
