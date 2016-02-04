package org.intellij.plugins.ceylon.ide.annotator;

import ceylon.collection.MutableList;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleValidator;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.typechecker.IdePhasedUnit;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing.IdeaModulesScanner;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing.IdeaRootFolderScanner;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaPlatformUtils_;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

public class TypeCheckerProvider implements ModuleComponent, ITypeCheckerProvider {

    private static final Logger LOGGER = Logger.getInstance(TypeCheckerProvider.class);
    private Module module;
    private TypeChecker typeChecker;
    private IdeaCeylonProjects ceylonModel;
    private boolean isReady;

    public TypeCheckerProvider(Module module) {
        this.module = module;
    }

    @Nullable
    public static TypeChecker getFor(PsiElement element) {
        if (element.getContainingFile() instanceof CeylonFile) {
            CeylonFile ceylonFile = (CeylonFile) element.getContainingFile();

            if (ceylonFile.getPhasedUnit() instanceof IdePhasedUnit) {
                IdePhasedUnit phasedUnit = (IdePhasedUnit) ceylonFile.getPhasedUnit();
                return phasedUnit.getTypeChecker();
            }

            //LOGGER.warn("CeylonFile has no IdePhasedUnit: " + ceylonFile.getVirtualFile().getCanonicalPath());

            // TODO .ceylon files loaded from .src archives don't belong to any module, what should we do?
            Module module = ModuleUtil.findModuleForFile(ceylonFile.getVirtualFile(), ceylonFile.getProject());

            if (module != null) {
                ITypeCheckerProvider provider = module.getComponent(ITypeCheckerProvider.class);

                if (((TypeCheckerProvider) provider).isReady) {
                    return provider.getTypeChecker();
                }
            }
        }

        return null;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public TypeChecker getTypeChecker() {
        return typeChecker;
    }

    @Override
    public void disposeComponent() {
        typeChecker = null;
        ceylonModel = null;
        module = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TypeCheckerProvider";
    }

    @Override
    public void projectOpened() {
    }

    public void typecheck() {
        if (ceylonModel == null) {
            return; // the module was just created, moduleAdded() will typecheck again
        }
        if (typeChecker == null) {
            ideaPlatformUtils_.get_().register();

            ProgressManager.getInstance().run(new Task.Backgroundable(module.getProject(),
                    "Preparing typechecker for module " + module.getName()) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    typeChecker = createTypeChecker();

                    parseCeylonModel();

                    setupCeylonLanguageSrc();

                    ApplicationManager.getApplication().runReadAction(new Runnable() {
                        @Override
                        public void run() {
                            fullTypeCheck();
                        }
                    });

                    isReady = true;

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            DaemonCodeAnalyzer.getInstance(module.getProject()).restart();
                        }
                    });
                }
            });
        } else {
            DaemonCodeAnalyzer.getInstance(module.getProject()).restart();
        }
    }

    private void fullTypeCheck() {
        List<PhasedUnit> phasedUnitsOfDependencies = new ArrayList<>();

        for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            for (PhasedUnit pu : phasedUnits.getPhasedUnits()) {
                phasedUnitsOfDependencies.add(pu);
            }
        }

        for (PhasedUnit pu : phasedUnitsOfDependencies) {
            pu.scanDeclarations();
        }

        for (PhasedUnit pu : phasedUnitsOfDependencies) {
            pu.scanTypeDeclarations();
        }

        for (PhasedUnit pu : phasedUnitsOfDependencies) {
            pu.analyseTypes();
        }

        BaseIdeModuleManager mm = (BaseIdeModuleManager) typeChecker.getPhasedUnits().getModuleManager();
        BaseIdeModelLoader loader = mm.getModelLoader();
        loader.loadPackage(loader.getLanguageModule(), "com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage(loader.getLanguageModule(), "ceylon.language", true);
        loader.loadPackage(loader.getLanguageModule(), "ceylon.language.descriptor", true);
        loader.loadPackageDescriptors();

        List<PhasedUnit> phasedUnits = typeChecker.getPhasedUnits().getPhasedUnits();
        for (PhasedUnit pu : phasedUnits) {
            if (!pu.isDeclarationsScanned()) {
                pu.validateTree();
                pu.scanDeclarations();
            }
        }
        for (PhasedUnit pu : phasedUnits) {
            if (!pu.isTypeDeclarationsScanned()) {
                pu.scanTypeDeclarations();
            }
        }
        for (PhasedUnit pu : phasedUnits) {
            if (!pu.isRefinementValidated()) {
                pu.validateRefinement();
            }
        }
        for (PhasedUnit pu : phasedUnits) {
            if (!pu.isFullyTyped()) {
                pu.analyseTypes();
                pu.analyseUsage();
            }
        }
        for (PhasedUnit pu : phasedUnits) {
            pu.analyseFlow();
        }
    }

    private void parseCeylonModel() {
        final IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);


        BaseIdeModuleManager mm = (BaseIdeModuleManager) typeChecker.getPhasedUnits().getModuleManager();
        mm.setTypeChecker(typeChecker);
        mm.getModuleSourceMapper().setTypeChecker(typeChecker);

        BaseIdeModelLoader loader = mm.getModelLoader();

        mm.prepareForTypeChecking();

        for (final VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
            final IdeaModulesScanner scanner = new IdeaModulesScanner(ceylonProject, sourceRoot);

            VfsUtilCore.visitChildrenRecursively(sourceRoot, new VirtualFileVisitor() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    return scanner.visitNativeResource(file);
                }
            });
        }

        TypeDescriptor fileVirtualFileTypeDescriptor = TypeDescriptor.klass(FileVirtualFile.class,
                TypeDescriptor.klass(VirtualFile.class),
                TypeDescriptor.klass(VirtualFile.class),
                TypeDescriptor.klass(VirtualFile.class));

        MutableList<FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile>> projectVirtualFiles
                = new ceylon.collection.ArrayList<>(fileVirtualFileTypeDescriptor);

        // todo separate sources from resources
        for (final VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
            final IdeaRootFolderScanner scanner = new IdeaRootFolderScanner(ceylonProject, sourceRoot, true, projectVirtualFiles);

            VfsUtilCore.visitChildrenRecursively(sourceRoot, new VirtualFileVisitor() {
                @Override
                public boolean visitFile(@NotNull VirtualFile file) {
                    return scanner.visitNativeResource(file);
                }
            });
        }

        loader.setupSourceFileObjects(typeChecker.getPhasedUnits().getPhasedUnits());
        typeChecker.getPhasedUnits().visitModules();

        ModuleValidator moduleValidator = new ModuleValidator(typeChecker.getContext(), typeChecker.getPhasedUnits());
        moduleValidator.verifyModuleDependencyTree();

        typeChecker.setPhasedUnitsOfDependencies(moduleValidator.getPhasedUnitsOfDependencies());

        for (PhasedUnits dependencyPhasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            loader.addSourceArchivePhasedUnits(dependencyPhasedUnits.getPhasedUnits());
        }

        loader.setModuleAndPackageUnits();
    }

    @Override
    public void projectClosed() {
        typeChecker = null;
        IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);
        ceylonProject.shutdownFileWatcher();

        isReady = false;
        if (ceylonModel != null) {
            ceylonModel.removeProject(module);
        }
    }

    @Override
    public void moduleAdded() {
        if (FacetManager.getInstance(module).getFacetByType(CeylonFacet.ID) == null) {
            return;
        }

        if (ceylonModel == null) {
            ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
        }
        ceylonModel.addProject(module);

        StartupManager.getInstance(module.getProject()).runWhenProjectIsInitialized(
                new Runnable() {
                    @Override
                    public void run() {
                        typecheck();
                    }
                }
        );
    }

    private void setupCeylonLanguageSrc() {
        if (typeChecker != null) {
            ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    AccessToken lock = ApplicationManager.getApplication().acquireWriteActionLock(TypeCheckerProvider.class);
                    IdeaModule languageModule = (IdeaModule) typeChecker.getPhasedUnits()
                            .getModuleManager().getModules().getLanguageModule();

                    ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();

                    LibraryTable tbl = model.getModuleLibraryTable();
                    Library lib = tbl.getLibraryByName("Ceylon Stuff");

                    if (lib == null) {
                        lib = tbl.createLibrary("Ceylon Stuff");
                    }
                    Library.ModifiableModel modifiableModel = lib.getModifiableModel();
                    try {
                        String srcFile = "jar://" + languageModule.getArtifact().getCanonicalPath() + "!/";
                        modifiableModel.addRoot(VirtualFileManager.getInstance().findFileByUrl(srcFile), OrderRootType.CLASSES);

                        modifiableModel.commit();
                        model.commit();
                    } catch (Exception e) {
                        modifiableModel.dispose();
                        model.dispose();
                    } finally {
                        lock.finish();
                    }
                }
            }, ModalityState.any());

            DumbService.getInstance(module.getProject()).waitForSmartMode();
        }
    }

    private TypeChecker createTypeChecker() {
        final IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);
        CeylonProjectConfig ceylonConfig = ceylonProject.getConfiguration();

        ceylonProject.setupFileWatcher();

        TypeCheckerBuilder builder = new TypeCheckerBuilder(ceylonModel.getVfs())
                .verbose(false)
                .usageWarnings(true);

        ceylon.language.String configRepo = ceylonProject.getIdeConfiguration().getSystemRepository();
        String systemRepo = configRepo == null ? null : interpolateVariablesInRepositoryPath(configRepo.toString());

        if (systemRepo == null) {
            systemRepo = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        }
        boolean offline = ceylonConfig.getOffline();
        File cwd = ceylonConfig.getProject().getRootDirectory();

        LOGGER.info("Using Ceylon system repository in " + systemRepo);

        final RepositoryManager repositoryManager = repoManager()
                .offline(offline)
                .cwd(cwd)
                .systemRepo(systemRepo)
//                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
                .isJDKIncluded(true)
                .buildManager();
        builder.setRepositoryManager(repositoryManager);

        builder.moduleManagerFactory(new IdeaModuleManagerFactory(repositoryManager, ceylonProject));

        return builder.getTypeChecker();
    }

    private String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        String pluginRepoPath = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        return repoPath.replace("${user.home}", userHomePath).replace("${ceylon.repo}", pluginRepoPath);
    }

}
