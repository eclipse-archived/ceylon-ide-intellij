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
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.model.loader.model.LazyModule;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

public class TypeCheckerProvider implements ModuleComponent, ITypeCheckerProvider {

    public static final Logger LOGGER = Logger.getInstance(TypeCheckerProvider.class);
    private final Module module;
    private TypeChecker typeChecker;
    IdeaCeylonProjects ceylonModel;

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

    public void initComponent() {
    }

    @Override
    public TypeChecker getTypeChecker() {
        return typeChecker;
    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "TypeCheckerProvider";
    }

    public void projectOpened() {
    }

    public void typecheck() {
        if (typeChecker == null) {
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

        typecheck();
    }

    public TypeChecker createTypeChecker() {
        CeylonProject<Module> ceylonProject = ceylonModel.getProject(module);
        CeylonProjectConfig<Module> ceylonConfig = ceylonProject.getConfiguration();

        TypeCheckerBuilder builder = new TypeCheckerBuilder()
                .verbose(false)
                .usageWarnings(true);


        String systemRepo = getFacetSystemRepo();
        if (systemRepo == null) {
            systemRepo = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        }
        boolean offline = ceylonConfig.getOffline();
        File cwd = ceylonConfig.getProject().getRootDirectory();

        LOGGER.info("Using Ceylon system repository in " + systemRepo);

        RepositoryManager repositoryManager = repoManager()
                .offline(offline)
                .cwd(cwd)
                .systemRepo(systemRepo)
//                .extraUserRepos(getReferencedProjectsOutputRepositories(project))
//                .logger(new EclipseLogger())
                .isJDKIncluded(true)
                .buildManager();
        builder.setRepositoryManager(repositoryManager);

        builder.moduleManagerFactory(new ModuleManagerFactory() {
            @Override
            public ModuleManager createModuleManager(final Context context) {
                // FIXME use a real LazyModuleManager to remove this hack
                return new ModuleManager() {
                    @Override
                    public void addImplicitImports() {
                        com.redhat.ceylon.model.typechecker.model.Module languageModule = getModules().getLanguageModule();
                        for (com.redhat.ceylon.model.typechecker.model.Module m : getModules().getListOfModules()) {
                            // Java modules don't depend on ceylon.language
                            if ((!(m instanceof LazyModule) || !m.isJava()) && !m.equals(languageModule)) {
                                // add ceylon.language if required
                                ModuleImport moduleImport = findImport(m, languageModule);
                                if (moduleImport == null) {
                                    moduleImport = new ModuleImport(languageModule, false, true);
                                    m.addImport(moduleImport);
                                }
                            }
                        }
                    }
                };
            }

            @Override
            public ModuleSourceMapper createModuleManagerUtil(Context context, ModuleManager moduleManager) {
                return new ModuleSourceMapper(context, moduleManager);
            }
        });

        for (VirtualFile sourceRoot : ModuleRootManager.getInstance(module).getSourceRoots()) {
            builder.addSrcDirectory(VFileAdapter.createInstance(sourceRoot));
        }

        long startTime = System.currentTimeMillis();
        TypeChecker checker = builder.getTypeChecker();
        LOGGER.info("Got type checker in " + (System.currentTimeMillis() - startTime) + "ms");

        startTime = System.currentTimeMillis();
        checker.process();
        LOGGER.info("Type checker process()ed in " + (System.currentTimeMillis() - startTime) + "ms");

        return checker;
    }

    private String getFacetSystemRepo() {
        String repo = null;
        CeylonFacet facet = CeylonFacet.forModule(module);

        if (facet != null) {
            CeylonFacetState state = facet.getConfiguration().getState();

            if (state != null) {
                repo = state.getSystemRepository();
            }
        }

        return StringUtils.isBlank(repo) ? null : interpolateVariablesInRepositoryPath(repo);
    }

    private String interpolateVariablesInRepositoryPath(String repoPath) {
        String userHomePath = System.getProperty("user.home");
        String pluginRepoPath = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        return repoPath.replace("${user.home}", userHomePath).replace("${ceylon.repo}", pluginRepoPath);
    }

}
