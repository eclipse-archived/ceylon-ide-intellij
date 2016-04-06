import ceylon.interop.java {
    CeylonIterable,
    javaClass
}

import com.intellij.openapi.application {
    WriteAction,
    Result,
    ApplicationManager
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.intellij.openapi.fileEditor {
    FileEditorManagerListener
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleManager
}
import com.intellij.openapi.roots {
    ModuleRootManager,
    OrderRootType
}
import com.intellij.openapi.ui {
    Messages
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil,
    VirtualFileManager,
    VirtualFileVisitor,
    VfsUtilCore,
    JarFileSystem
}
import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.compiler.typechecker.util {
    ModuleManagerFactory
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.model.parsing {
    RootFolderScanner
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitorChild
}
import com.redhat.ceylon.model.typechecker.util {
    TCModManager=ModuleManager
}

import java.io {
    File,
    IOException
}
import java.lang {
    Void,
    System
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerInvoker
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    FileWatcher
}
import org.jetbrains.jps.model.java {
    JavaSourceRootType,
    JavaResourceRootType
}
import com.intellij.openapi.roots.impl.libraries {
    ProjectLibraryTable
}

shared class IdeaCeylonProject(ideArtifact, model)
        extends CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>() {
    
    shared actual Module ideArtifact;
    shared actual IdeaCeylonProjects model;
    shared actual String name => ideArtifact.name;

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
    
    // TODO check if the module is open?
    shared actual Boolean nativeProjectIsAccessible => true;
    
    shared actual {Module*} referencedNativeProjects(Module mod)
            => ModuleRootManager.getInstance(mod)
                .dependencies.array.coalesced;
    
    shared actual {Module*} referencingNativeProjects(Module mod)
            => CeylonIterable(ModuleManager.getInstance(mod.project)
                .getModuleDependentModules(mod));
    
    late FileWatcher watcher;
    
    shared void setupFileWatcher() {
        watcher = FileWatcher(this);
        VirtualFileManager.instance.addVirtualFileListener(watcher);
        ideArtifact.project.messageBus.connect()
                .subscribe(FileEditorManagerListener.\iFILE_EDITOR_MANAGER, watcher);
    }
    
    shared void shutdownFileWatcher() {
        VirtualFileManager.instance.removeVirtualFileListener(watcher);
    }
    
    shared actual void createOverridesProblemMarker(Exception ex, File absoluteFile, Integer overridesLine, Integer overridesColumn) {
        // TODO
    }
    
    shared actual void removeOverridesProblemMarker() {
        // TODO
    }
    
    shared actual String systemRepository 
            => interpolateVariablesInRepositoryPath(ideConfiguration.systemRepository else "${ceylon.repo}");
    
    String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        value ext = Extensions.getExtensions(ITypeCheckerInvoker.\iEP_NAME).get(0);
        String pluginRepoPath = ext.embeddedCeylonRepository.absolutePath;
        return repoPath.replace("${user.home}", userHomePath).replace("${ceylon.repo}", pluginRepoPath);
    }
    
    shared actual {VirtualFile*} resourceNativeFolders {
        value roots = ModuleRootManager.getInstance(ideaModule)
                ?.getSourceRoots(JavaResourceRootType.\iRESOURCE);
        
        return if (exists roots) then CeylonIterable(roots) else empty;
    }
    
    shared actual {VirtualFile*} sourceNativeFolders {
        value roots = ModuleRootManager.getInstance(ideaModule)
                ?.getSourceRoots(JavaSourceRootType.\iSOURCE);
        
        return if (exists roots) then CeylonIterable(roots) else empty;
    }
    
    shared actual void scanRootFolder(RootFolderScanner<Module,VirtualFile,VirtualFile,VirtualFile> scanner) {
        VfsUtilCore.visitChildrenRecursively(scanner.nativeRootDir, 
            object extends VirtualFileVisitor<Nothing>() {
                visitFile(VirtualFile file) => scanner.visitNativeResource(file);
            }
        );
    }
    shared actual void completeCeylonModelParsing(BaseProgressMonitorChild monitor) {}
    
    shared actual ModuleManagerFactory moduleManagerFactory => 
            object satisfies ModuleManagerFactory {
        
                createModuleManager(Context context)
                        => IdeaModuleManager(context.repositoryManager, outer);
                
                shared actual IdeaModuleSourceMapper
                createModuleManagerUtil(Context context, TCModManager moduleManager) {
                    assert(is IdeaModuleManager moduleManager);
                    return IdeaModuleSourceMapper(context, moduleManager);
                }
            };
    
    value libraryName => "Ceylon dependencies";
    
    shared void addLibrary(String jarFile, Boolean clear = false) {
        value lock = ApplicationManager.application.acquireWriteActionLock(javaClass<IdeaCeylonProject>());
        value mm = ProjectLibraryTable.getInstance(ideArtifact.project).modifiableModel;
        value lib = mm.getLibraryByName(libraryName) else mm.createLibrary(libraryName);
        value model = lib.modifiableModel;
        
        try {
            if (clear) {
                for (url in model.getUrls(OrderRootType.\iCLASSES)) {
                    model.removeRoot(url.string, OrderRootType.\iCLASSES);
                }
            }

            value srcFile = VirtualFileManager.instance
                .findFileByUrl(JarFileSystem.\iPROTOCOL_PREFIX + jarFile + JarFileSystem.\iJAR_SEPARATOR);
            
            model.addRoot(srcFile, OrderRootType.\iCLASSES);
            
            value mrm = ModuleRootManager.getInstance(ideArtifact).modifiableModel;
            if (!exists entry = mrm.findLibraryOrderEntry(lib)) {
                mrm.addLibraryEntry(lib);
            }
            
            model.commit();
            mrm.commit();
            mm.commit();
        } catch (e) {
            model.dispose();
        } finally {
            lock.finish();
        }
    }
}
