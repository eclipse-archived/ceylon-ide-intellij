import ceylon.collection {
    ArrayList
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
    ModalityState,
    ApplicationInfo
}
import com.intellij.openapi.externalSystem.service.project {
    IdeModifiableModelsProviderImpl
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager,
    Task,
    ProgressIndicator,
    ProcessCanceledException
}
import com.intellij.openapi.progress.util {
    ProgressIndicatorBase
}
import com.intellij.openapi.project {
    DumbService {
        dumbService=getInstance
    }
}
import com.intellij.openapi.roots {
    ModuleRootManager,
    OrderRootType,
    LibraryOrderEntry,
    DependencyScope,
    ModifiableRootModel
}
import com.intellij.openapi.ui {
    Messages
}
import com.intellij.openapi.util {
    Key,
    Ref
}
import com.intellij.openapi.util.io {
    FileUtil
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil,
    VirtualFileManager {
        virtualFileManager=instance
    },
    JarFileSystem,
    VfsUtilCore,
    LocalFileSystem
}
import com.intellij.psi {
    PsiFile,
    JavaPsiFacade
}
import com.intellij.psi.search {
    GlobalSearchScope
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
import com.redhat.ceylon.ide.common.typechecker {
    IdePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitorChild,
    BaseProgressMonitor
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult,
    ArtifactResultType
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}
import com.redhat.ceylon.model.typechecker.util {
    TCModManager=ModuleManager
}

import java.io {
    File,
    IOException
}
import java.lang {
    Types {
        nativeString
    },
    System,
    Thread
}
import java.lang.ref {
    WeakReference
}

import org.intellij.plugins.ceylon.ide.platform {
    ideaPlatformUtils
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin
}

shared class IdeaCeylonProject(ideArtifact, model)
        extends CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>() {
    variable Boolean languageModuleAdded = false;
    
    shared object nativeFolderProperties {
        shared Key<WeakReference<Package>> packageModel
                = Key<WeakReference<Package>>("CeylonPlugin.nativeFolder_packageModel");
        shared Key<WeakReference<FolderVirtualFileAlias>> root
                = Key<WeakReference<FolderVirtualFileAlias>>("CeylonPlugin.nativeFolder_root");
        shared Key<Boolean> rootIsSource = Key<Boolean>("CeylonPlugin.nativeFolder_rootIsSource");
    }

    shared variable Boolean validatingDependencies = false;
    shared ArrayList<ArtifactResult> dependenciesToAdd = ArrayList<ArtifactResult>();

    shared void addDependencyToClasspath(ArtifactResult dependency) {
        if (validatingDependencies) {
            dependenciesToAdd.add(dependency);
        } else {
            ideaPlatformUtils.log(Status._ERROR,
                "addDependencyToClasspath should only be called when validating dependencies");
        }
    }

    value modality => ApplicationInfo.instance.build.baselineVersion >= 163
    then ModalityState.defaultModalityState()
    else ModalityState.any();

    object addModuleArchiveHook
            satisfies BuildHook<Module, VirtualFile, VirtualFile, VirtualFile> {

        ArtifactResult? findLanguageCar() {
            String moduleName = "ceylon.language";
            String moduleVersion = TypeChecker.languageModuleVersion;

            return repositoryManager.getArtifactResult(
                ArtifactContext(null, moduleName, moduleVersion, ArtifactContext.car)
            );
        }

        shared actual void beforeClasspathResolution(CeylonProjectBuildAlias build, CeylonProjectBuildAlias.State state) {
        }

        shared actual void repositoryManagerReset(CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile> ceylonProject) {
            languageModuleAdded = false;
        }

        shared actual void beforeDependencyTreeValidation(CeylonProjectAlias ceylonProject,
            BaseProgressMonitor.Progress progress) {

            validatingDependencies = true;
            dependenciesToAdd.clear();

            if (exists languageModuleArtifact = findLanguageCar()) {
                dependenciesToAdd.add(languageModuleArtifact);
            } else {
                platformUtils.log(Status._ERROR, "Could not locate ceylon.language.car");
            }
        }

        shared actual void afterDependencyTreeValidation(CeylonProjectAlias ceylonProject,
            BaseProgressMonitor.Progress progress) {
            Thread.currentThread().contextClassLoader
                    = Types.classForType<IdeaCeylonProject>().classLoader;

            value sourcesToAttach = Ref<{ArtifactContext*}>();

            void runnable() {
                try {
                    sourcesToAttach.set(addLibraries(dependenciesToAdd, true));
                    validatingDependencies = false;
                } catch (IOException e) {
                    platformUtils.log(Status._ERROR, "Can't add required artifacts to classpath", e);
                }
            }
            application.invokeAndWait(runnable, ModalityState.nonModal);

            dumbService(model.ideaProject).waitForSmartMode();

            if (exists sources = sourcesToAttach.get()) {
                ProgressManager.instance.runProcessWithProgressAsynchronously(
                    object extends Task.Backgroundable(ceylonProject.ideArtifact.project, "Attaching sources to dependencies") {
                        shared actual void run(ProgressIndicator progressIndicator) {
                            variable value counter = 0;
                            value artifacts = sources.collect(
                                (ctx) {
                                    progressIndicator.fraction = counter.float / sources.size;
                                    progressIndicator.text2 = "Attaching " + ctx.string;
                                    return ctx -> (repositoryManager.getArtifactResult(ctx) else null);
                                }
                            );
                            application.invokeAndWait(() => attachSources(artifacts), modality);
                        }
                } , ProgressIndicatorBase());
            }

            // Special case for Android Studio: when the Ceylon facet is added to a module,
            // it will trigger a Gradle build which will prevent depencies we add to the classpath
            // from being indexed immediately. We *have* to wait for them to be indexed,
            // otherwise it leads to errors like https://github.com/ceylon/ceylon-ide-intellij/issues/497
            if (compileToJava,
                !model.ideaProject.isDisposed()) {
                value ds = DumbService.getInstance(model.ideaProject);
                value facade = JavaPsiFacade.getInstance(model.ideaProject);
                value scope = GlobalSearchScope.moduleWithLibrariesScope(ideArtifact);
                value ceylonCls = "ceylon.language.String";

                Boolean ceylonLanguageIndexed() {
                    value ref = Ref<Boolean>();
                    ds.runReadActionInSmartMode(()
                        => ref.set(facade.findClass(ceylonCls, scope) exists));
                    return ref.get();
                }

                variable value counter = 0;

                while (!ceylonLanguageIndexed(), counter < 5) {
                    progress.subTask("Waiting for indices");
                    counter++;
                    Thread.sleep(1000);
                }

                if (!ceylonLanguageIndexed()) {
                    throw ProcessCanceledException();
                }

                progress.subTask("Scanning source archives");
            }
        }
    }

    shared actual Module ideArtifact;
    shared actual IdeaCeylonProjects model;
    shared actual String name => ideArtifact.name;

    shared actual Boolean loadBinariesFirst => true;

    shared actual Boolean loadInterProjectDependenciesFromSourcesFirst => true;
            
    shared Module ideaModule => ideArtifact;

    VirtualFile getDefaultRoot() {
        if (exists file = ideaModule.moduleFile) {
            return file.parent;
        }

        value path = ideaModule.moduleFilePath;
        if (exists lastSlash = path.lastOccurrence('/')) {
            String parentPath = LocalFileSystem.protocolPrefix + path[0..lastSlash];
            if (exists file = virtualFileManager.findFileByUrl(parentPath)) {
                return file;
            }
        }

        if (application.unitTestMode) {
            return ideaModule.project.baseDir;
        }

        throw Exception("Couldn't get module root for ``path``");
    }

    shared VirtualFile moduleRoot {
        value defaultRoot = getDefaultRoot();
        if (exists mmr = ModuleRootManager.getInstance(ideaModule),
            mmr.contentRoots.size == 1) {
            return mmr.contentRoots[0] else defaultRoot;
        }
        else {
            return defaultRoot;
        }
    }

    shared actual File rootDirectory
            => VfsUtilCore.virtualToIoFile(moduleRoot);

    VirtualFile? findModuleFile(String moduleRelativePath)
            => moduleRoot.findFileByRelativePath(moduleRelativePath);

    VirtualFile? findModuleFileWithRefresh(String moduleRelativePath)
            => VfsUtil.findFileByIoFile(File(rootDirectory, moduleRelativePath), true);

    shared actual void createNewOutputFolder(String relativePath) {
        function createDirectory()
            =>  object extends WriteAction<Nothing>() {
                    shared actual void run(Result<Nothing> result) {
                        // Our dialog actually allows paths outside the current project
                        if (FileUtil.isAbsolute(relativePath)) {
                            VfsUtil.createDirectoryIfMissing(relativePath);
                        } else {
                            VfsUtil.createDirectoryIfMissing(moduleRoot, relativePath);
                        }
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
        if (exists oldOutputRepoFolder = findModuleFile(folderProjectRelativePath),
            oldOutputRepoFolder != moduleRoot,
            !oldOutputRepoFolder in sourceNativeFolders) {
            if (Messages.showYesNoDialog(ideaModule.project,
                    "The Ceylon output repository has changed.
                     Do you want to remove the old output repository folder \
                     '`` oldOutputRepoFolder.path ``' and all its contents ?",
                    "Changing Ceylon output repository",
                    Messages.questionIcon) == Messages.yes) {
                try {
                    object extends WriteAction<Nothing>() {
                        run(Result<Nothing> result)
                                => oldOutputRepoFolder.delete(outer);
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

    shared actual void refreshConfigFile(String projectRelativePath) {
        value configFile = findModuleFile(projectRelativePath);
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
    
    shared actual String systemRepository =>
            let (path = if (exists val = ideConfiguration.systemRepository, !val.empty) then val else null)
            interpolateVariablesInRepositoryPath(path else "\${ceylon.repo}");
    
    String interpolateVariablesInRepositoryPath(String repoPath) {
        value userHomePath = System.getProperty("user.home");
        value pluginRepoPath = CeylonIdePlugin.embeddedCeylonRepository.absolutePath;
        return repoPath.replace("\${user.home}", userHomePath).replace("\${ceylon.repo}", pluginRepoPath);
    }

    shared actual void completeCeylonModelParsing(BaseProgressMonitorChild monitor) {}
    
    shared actual ModuleManagerFactory moduleManagerFactory => 
            object satisfies ModuleManagerFactory {
        
                createModuleManager(Context context)
                        => IdeaModuleManager(context.repositoryManager, model, outer);
                
                shared actual IdeaModuleSourceMapper
                createModuleManagerUtil(Context context, TCModManager moduleManager) {
                    assert(is IdeaModuleManager moduleManager);
                    return IdeaModuleSourceMapper(context, moduleManager);
                }
            };

    void doWithLibraryModel(void func(IdeModifiableModelsProviderImpl provider, ModifiableRootModel mrm)) =>
            application.runWriteAction(() {
                value provider = IdeModifiableModelsProviderImpl(ideArtifact.project);
                value mrm = provider.getModifiableRootModel(ideArtifact);

                try {
                    func(provider, mrm);

                    provider.commit();
                } catch (e) {
                    platformUtils.log(Status._ERROR, "Couldn't modify library", e);
                    provider.dispose();
                }
            });

    {ArtifactContext*} addLibraries(List<ArtifactResult> libraries, Boolean clear = false) {
        doWithLibraryModel((provider, mrm) {
            if (clear) {
                for (e in mrm.orderEntries) {
                    if (is LibraryOrderEntry e,
                        exists libName = e.libraryName,
                        libName.startsWith("Ceylon: ") || libName == "Ceylon dependencies") {
                        mrm.removeOrderEntry(e);
                    }
                }
            }

            for (artifact in libraries.sort((x, y) => x.name().compare(y.name()))) {
                value libraryName = "Ceylon: " + artifact.name() + "/" + artifact.version();
                value lib = provider.getLibraryByName(libraryName)
                else provider.createLibrary(libraryName);
                value libModel = provider.getModifiableLibraryModel(lib);

                void updateUrl(OrderRootType type, VirtualFile file) {
                    if (!nativeString(file.string) in libModel.getUrls(type).iterable) {
                        libModel.addRoot(file, type);
                    }
                }

                value carFile = VirtualFileManager.instance
                    .refreshAndFindFileByUrl(JarFileSystem.protocolPrefix + artifact.artifact().canonicalPath + JarFileSystem.jarSeparator);

                if (exists carFile) {
                    updateUrl(OrderRootType.classes, carFile);
                }

                if (exists entry = mrm.findLibraryOrderEntry(lib)) {
                    entry.scope = DependencyScope.provided;
                } else {
                    value newEntry = mrm.addLibraryEntry(lib);
                    newEntry.scope = DependencyScope.provided;
                }
            }

        });

        return {
            for (artifact in libraries.sort((x, y) => x.name().compare(y.name())))
                let(type = artifact.type() == ArtifactResultType.maven then ArtifactContext.legacySrc else ArtifactContext.src)
                ArtifactContext(artifact.namespace(), artifact.name(), artifact.version(), type)
        };

    }

    void attachSources(<ArtifactContext->ArtifactResult?>[] artifacts) =>
            doWithLibraryModel((provider, mrm) {
                for (sourceContext -> sourceArtifact in artifacts) {
                    if (exists sourceArtifact) {
                        value libraryName = "Ceylon: " + sourceContext.name + "/" + sourceContext.version;
                        value lib = provider.getLibraryByName(libraryName) else provider.createLibrary(libraryName);
                        value libModel = provider.getModifiableLibraryModel(lib);
                        value srcFile = VirtualFileManager.instance
                            .findFileByUrl(JarFileSystem.protocolPrefix + sourceArtifact.artifact().canonicalPath + JarFileSystem.jarSeparator);

                        if (exists srcFile) {
                            if (!nativeString(srcFile.string) in libModel.getUrls(OrderRootType.sources).iterable) {
                                libModel.addRoot(srcFile, OrderRootType.sources);
                            }
                        }
                    }
                }
            });

    shared Boolean isAndroid {
        for (f in FacetManager.getInstance(ideaModule).allFacets) {
            if (f.type.id.string == "android-gradle") {
                return true;
            }
        }

        return false;
    }

    value srcPath => "src/main/ceylon";
    value otherSrcPath => "build/generated/source/r/debug";
    value repoPath => "./build/intermediates/ceylon-android/repository";

    shared void setupForAndroid(String jdkProvider) {
        if (!srcPath in configuration.projectSourceDirectories) {
            configuration.projectSourceDirectories = {
                srcPath,
                otherSrcPath,
                *configuration.projectSourceDirectories
            };
        }

        if (!repoPath in configuration.projectLocalRepos) {
            configuration.projectLocalRepos = [
                repoPath,
                *configuration.projectLocalRepos
            ];
        }

        configuration.projectJdkProvider = jdkProvider;
    }

    shared void clean() {
        sourceNativeFolders.each(removeFolderFromModel);
    }

    buildHooks => { addModuleArchiveHook };

    shared void addSourceRoot(VirtualFile sourceRoot) {
        if (exists relativePath = VfsUtil.getRelativePath(sourceRoot, moduleRoot)) {
            configuration.projectSourceDirectories =
                {relativePath, *configuration.sourceDirectories};
        }
    }
}

shared IdeaCeylonProject? findProjectForFile(PsiFile file) {
    if (is CeylonFile file,
        is IdePhasedUnit pu =
                file.availableAnalysisResult?.typecheckedRootNode
                else file.localAnalyzer?.result?.lastPhasedUnit,
        is IdeaModuleSourceMapper msm = pu.moduleSourceMapper,
        is IdeaCeylonProject project = msm.ceylonProject) {
        return project;
    }
    else {
        return getCeylonProject(file);
    }
}
