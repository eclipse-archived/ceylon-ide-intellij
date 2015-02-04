import com.redhat.ceylon.ide.common {
    CeylonProject
}
import java.io {
    File,
    IOException
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil
}
import com.intellij.openapi.application {
    WriteAction,
    Result
}
import com.intellij.openapi.ui {
    Messages
}
import java.lang {
    Void
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.roots {
    ModuleRootManager
}

shared class IdeaCeylonProject(ideArtifact) extends CeylonProject<Module>() {
    shared actual Module ideArtifact;
    
    shared Module ideaModule => ideArtifact;
    
    VirtualFile moduleRoot 
            => let (defaultRoot = ideaModule.moduleFile.parent)
                    if (exists contentsRoot = ModuleRootManager.getInstance(ideaModule)?.contentRoots)
                        then ( contentsRoot.array.first else defaultRoot)
                        else defaultRoot;
    
    shared actual File rootDirectory
            => VfsUtil.virtualToIoFile(moduleRoot);

    VirtualFile? findModuleFile(String moduleRelativePath) 
            => moduleRoot.findFileByRelativePath(moduleRelativePath);
    
    VirtualFile? findModuleFileWithRefresh(String moduleRelativePath) 
            => VfsUtil.findFileByIoFile(File(rootDirectory, moduleRelativePath), true);

    shared actual void createNewOutputFolder(String folderModuleRelativePath) {
        function createDirectory()
            =>  object extends WriteAction<Void>() {
                    shared actual void run(Result<Void> result) {
                        VfsUtil.createDirectoryIfMissing(moduleRoot, folderModuleRelativePath);
                    }
                }.execute().throwException();
        
        if (exists outputFolder = findModuleFileWithRefresh(folderModuleRelativePath)) {
            outputFolder.refresh(false, false);
            if (! outputFolder.\iexists()) {
                createDirectory();
            }
        } else {
            createDirectory();
        }
    }
    
    shared actual void deleteOldOutputFolder(String folderProjectRelativePath) {
        VirtualFile? oldOutputRepoFolder = findModuleFile(folderProjectRelativePath);
        if(exists oldOutputRepoFolder) {
            if (Messages.showYesNoDialog(ideaModule.project, 
                    "The Ceylon output repository has changed.
                     Do you want to remove the old output repository folder '`` oldOutputRepoFolder.path ``' and all its contents ?",
                    "Changing Ceylon output repository",
                    Messages.questionIcon) == Messages.\iYES) {
                try {
                    object extends WriteAction<Void>() {
                        shared actual void run(Result<Void> result) {
                            oldOutputRepoFolder.delete(outer);
                        }
                    }.execute().throwException();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO : if we keep the old output folder, remove the "Derived flag"
                
            }
        }
    }
    
    shared actual Boolean hasConfigFile => findModuleFile(ceylonConfigFileProjectRelativePath) exists;
    
    shared actual void refreshConfigFile() {
        value configFile = findModuleFile(ceylonConfigFileProjectRelativePath);
        if (exists configFile) {
            configFile.refresh(false, false); // TODO check if this is OK to do a synchronous refresh
        } else {
            moduleRoot.refresh(false, false);
        }
    }
    
}


