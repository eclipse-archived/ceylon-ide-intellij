import ceylon.interop.java {
    javaClass,
    JavaRunnable
}

import com.intellij.facet {
    FacetManager
}
import com.intellij.openapi.application {
    WriteAction,
    Result,
    ApplicationManager {
        application
    },
    ModalityState
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.intellij.openapi.externalSystem.service.project {
    IdeModifiableModelsProviderImpl
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleUtil
}
import com.intellij.openapi.project {
    DumbService {
        dumbService=getInstance
    }
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
    JarFileSystem
}
import com.redhat.ceylon.cmr.api {
    ArtifactContext
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.compiler.typechecker.util {
    ModuleManagerFactory
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    BuildHook
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
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
    vfsKeychain
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class IdeaCeylonProject(ideArtifact, model)
        extends CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>() {
    variable Boolean languageModuleAdded = false;

    object addModuleArchiveHook
            satisfies BuildHook<Module, VirtualFile, VirtualFile, VirtualFile> {

        File? findLanguageCar() {
            String moduleName = "ceylon.language";
            String moduleVersion = TypeChecker.\iLANGUAGE_MODULE_VERSION;

            return repositoryManager.getArtifact(
                ArtifactContext(moduleName, moduleVersion, ArtifactContext.\iCAR)
            );
        }

        shared actual void beforeClasspathResolution(CeylonProjectBuildAlias build, CeylonProjectBuildAlias.State state) {
            if (! languageModuleAdded) {
                if (exists languageModuleArtifact = findLanguageCar()) {
                    value runnable = JavaRunnable(() {
                        try {
                            value path = languageModuleArtifact.canonicalPath;
                            addLibrary(path, true);
                        } catch (IOException e) {
                            platformUtils.log(Status._ERROR,
                                "Can't add ceylon language to classpath", e);
                        }
                    });
                    application.invokeAndWait(runnable, ModalityState.any());

                    dumbService(model.ideaProject).waitForSmartMode();
                    languageModuleAdded = true;
                } else {
                    platformUtils.log(Status._ERROR, "Could not locate ceylon.language.car");
                }
            }
        }

        shared actual void repositoryManagerReset(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject) {
            languageModuleAdded = false;
        }
    }


    shared actual Module ideArtifact;
    shared actual IdeaCeylonProjects model;
    shared actual String name => ideArtifact.name;

    shared actual Boolean loadBinariesFirst => true;
            
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

        if (application.unitTestMode) {
            return ideaModule.project.baseDir;
        }
        throw Exception("Couldn't get module root for ``path``");
    }

    shared VirtualFile moduleRoot
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
        value lock = application.acquireWriteActionLock(javaClass<IdeaCeylonProject>());
        value provider = IdeModifiableModelsProviderImpl(ideArtifact.project);
        value lib = provider.getLibraryByName(libraryName) else provider.createLibrary(libraryName);
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
            
            value mrm = provider.getModifiableRootModel(ideArtifact);
            if (!exists entry = mrm.findLibraryOrderEntry(lib)) {
                mrm.addLibraryEntry(lib);
            }
            
            model.commit();
            provider.commit();
        } catch (e) {
            model.dispose();
        } finally {
            lock.finish();
        }
    }

    shared Boolean isAndroid {
        for (f in FacetManager.getInstance(ideaModule).allFacets) {
            if (f.type.id.string == "android-gradle") {
                return true;
            }
        }

        return false;
    }

    value srcPath => "src/main/ceylon";
    value repoPath => "./build/intermediates/ceylon-android/repository";

    shared void setupForAndroid(String jdkProvider) {
        if (!configuration.projectSourceDirectories.contains(srcPath)) {
            configuration.projectSourceDirectories = {
                srcPath,
                *configuration.projectSourceDirectories
            };
        }

        if (!configuration.projectLocalRepos.contains(repoPath)) {
            configuration.projectLocalRepos = {
                repoPath,
                *configuration.projectLocalRepos
            };
        }

        configuration.projectJdkProvider = jdkProvider;
    }

    shared void clean() {
        sourceNativeFolders.each(removeFolderFromModel);
        vfsKeychain.clear(ideaModule);
    }

    buildHooks => { addModuleArchiveHook };
}

shared IdeaCeylonProject? findProjectForFile(CeylonFile file) {
    
    if (exists projects = file.project.getComponent(javaClass<IdeaCeylonProjects>()),
        exists mod = ModuleUtil.findModuleForFile(file.virtualFile, file.project),
        is IdeaCeylonProject project = projects.getProject(mod)) {
        
        return project;
    }
    
    return null;
}