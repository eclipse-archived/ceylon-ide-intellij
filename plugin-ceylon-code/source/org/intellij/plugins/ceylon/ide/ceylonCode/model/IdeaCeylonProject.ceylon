import ceylon.interop.java {
    javaClass,
    CeylonIterable
}

import com.intellij.openapi.application {
    WriteAction,
    Result,
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleManager
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import com.intellij.openapi.ui {
    Messages
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil,
    VirtualFileManager
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    ModuleDependencies
}

import java.io {
    File,
    IOException
}
import java.lang {
    Void
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}

shared class IdeaCeylonProject(ideArtifact, model)
        extends CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>() {
    
    shared actual Module ideArtifact;
    shared actual IdeaCeylonProjects model;

    shared Module ideaModule => ideArtifact;

    VirtualFile getDefaultRoot() {
        if (exists file = ideaModule.moduleFile) {
            return file.parent;
        }

        value path = ideaModule.moduleFilePath;
        Integer? lastSlash = path.lastOccurrence('/');
        if (exists lastSlash) {
            String parentPath = path.span(0, lastSlash);
            return VirtualFileManager.instance
                    .findFileByUrl("file://``parentPath``");
        }

        if (ApplicationManager.application.unitTestMode) {
            return ideaModule.project.baseDir;
        }
        throw Exception("Couldn't get module root for ``path``");
    }

    VirtualFile moduleRoot
            => let (defaultRoot = getDefaultRoot())
                    if (exists contentsRoot = ModuleRootManager
                            .getInstance(ideaModule)?.contentRoots,
                        contentsRoot.array.size == 1)
                        then ( contentsRoot.array.first else defaultRoot)
                        else defaultRoot;

    shared actual File rootDirectory
            => VfsUtil.virtualToIoFile(moduleRoot);

    VirtualFile? findModuleFile(String moduleRelativePath)
            => moduleRoot.findFileByRelativePath(moduleRelativePath);

    VirtualFile? findModuleFileWithRefresh(String moduleRelativePath)
            => VfsUtil.findFileByIoFile(File(rootDirectory, moduleRelativePath), true);

    shared actual void createNewOutputFolder(String relativePath) {
        function createDirectory()
            =>  object extends WriteAction<Void>() {
                    shared actual void run(Result<Void> result) {
                        VfsUtil.createDirectoryIfMissing(moduleRoot, relativePath);
                    }
                }.execute().throwException();

        if (exists outputFolder = findModuleFileWithRefresh(relativePath)) {
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
                     Do you want to remove the old output repository folder \
                     '`` oldOutputRepoFolder.path ``' and all its contents ?",
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

    shared actual Boolean hasConfigFile
            => findModuleFile(ceylonConfigFileProjectRelativePath) exists;

    shared actual void refreshConfigFile() {
        value configFile = findModuleFile(ceylonConfigFileProjectRelativePath);
        if (exists configFile) {
            // TODO check if this is OK to do a synchronous refresh
            configFile.refresh(false, false);
        } else {
            moduleRoot.refresh(false, false);
        }
    }

    // TODO : needs to be implemented
    shared actual Boolean synchronizedWithConfiguration => true;
    
    shared actual Boolean compileToJava
            => ideConfiguration.compileToJvm else false;
    
    shared actual Boolean compileToJs
            => ideConfiguration.compileToJs else false;
    
    // TODO
    shared actual ModuleDependencies moduleDependencies => nothing;
    
    // TODO check if the module is open?
    shared actual Boolean nativeProjectIsAccessible => true;
    
    shared actual {Module*} referencedNativeProjects(Module mod)
            => ModuleRootManager.getInstance(mod)
                .dependencies.array.coalesced;
    
    shared actual {Module*} referencingNativeProjects(Module mod)
            => CeylonIterable(ModuleManager.getInstance(mod.project)
                .getModuleDependentModules(mod));
    
    shared actual TypeChecker? typechecker
            => ideArtifact.getComponent(javaClass<ITypeCheckerProvider>())?.typeChecker;
     
}

