package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModuleManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModuleSourceMapper;
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
    private final Module module;
    private TypeChecker typeChecker;
    private IdeaCeylonProjects ceylonModel;

    public TypeCheckerProvider(Module module) {
        this.module = module;
    }

    @Nullable
    public static TypeChecker getFor(PsiElement element) {
        if (element.getContainingFile() instanceof CeylonFile) {
            CeylonFile ceylonFile = (CeylonFile) element.getContainingFile();

            // TODO .ceylon files loaded from .src archives don't belong to any module, what should we do?
            Module module = ModuleUtil.findModuleForFile(ceylonFile.getVirtualFile(), ceylonFile.getProject());

            if (module != null) {
                return module.getComponent(ITypeCheckerProvider.class).getTypeChecker();
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

            ProgressManager.getInstance().run(new Task.Backgroundable(module.getProject(), "Preparing typechecker for module " + module.getName()) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    typeChecker = createTypeChecker();

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

    @Override
    public void projectClosed() {
        typeChecker = null;
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

        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
        // TODO repo manager, and move this somewhere else probably
        String carFile = "jar:///Users/bastien/Dev/ceylon/ceylon/dist/dist/repo/ceylon/language/1.2.1/ceylon.language-1.2.1.car!/";
        LibraryTable tbl = model.getModuleLibraryTable();

        Library lib = tbl.getLibraryByName("Ceylon Stuff");
        if (lib == null) {
            lib = tbl.createLibrary("Ceylon Stuff");
        }
        Library.ModifiableModel modifiableModel = lib.getModifiableModel();
        modifiableModel.addRoot(VirtualFileManager.getInstance().findFileByUrl(carFile), OrderRootType.CLASSES);

        modifiableModel.commit();
        model.commit();

        StartupManager.getInstance(module.getProject()).runWhenProjectIsInitialized(
                new Runnable() {
                    @Override
                    public void run() {
                        typecheck();
                    }
                }
        );
    }

    private TypeChecker createTypeChecker() {
        final IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);
        CeylonProjectConfig ceylonConfig = ceylonProject.getConfiguration();

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

        builder.moduleManagerFactory(new ModuleManagerFactory() {
            @Override
            public ModuleManager createModuleManager(final Context context) {
                return new IdeaModuleManager(repositoryManager, ceylonProject);
            }

            @Override
            public ModuleSourceMapper createModuleManagerUtil(Context context, ModuleManager moduleManager) {
                return new IdeaModuleSourceMapper(context, (IdeaModuleManager) moduleManager);
            }
        });

        for (VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
            builder.addSrcDirectory(VFileAdapter.createInstance(sourceRoot));
        }

        long startTime = System.currentTimeMillis();
        TypeChecker checker = builder.getTypeChecker();
        LOGGER.info("Got type checker in " + (System.currentTimeMillis() - startTime) + "ms");

        BaseIdeModuleManager mm = (BaseIdeModuleManager) checker.getPhasedUnits().getModuleManager();
        mm.setTypeChecker(checker);
        mm.getModuleSourceMapper().setTypeChecker(checker);

        BaseIdeModelLoader loader = mm.getModelLoader();
        loader.loadPackage(loader.getLanguageModule(), "com.redhat.ceylon.compiler.java.metadata", true);
        loader.loadPackage(loader.getLanguageModule(), "ceylon.language", true);
        loader.loadPackage(loader.getLanguageModule(), "ceylon.language.descriptor", true);
        loader.loadPackageDescriptors();

        startTime = System.currentTimeMillis();
        checker.process();
        LOGGER.info("Type checker process()ed in " + (System.currentTimeMillis() - startTime) + "ms");

        List<PhasedUnit> phasedUnitsOfDependencies = new ArrayList<>();

        for (PhasedUnits phasedUnits : checker.getPhasedUnitsOfDependencies()) {
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

        return checker;
    }

    private String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        String pluginRepoPath = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        return repoPath.replace("${user.home}", userHomePath).replace("${ceylon.repo}", pluginRepoPath);
    }

}
