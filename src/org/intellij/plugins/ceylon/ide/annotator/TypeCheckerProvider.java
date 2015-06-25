package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
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
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

public class TypeCheckerProvider implements ModuleComponent {

    private final Module module;
    private TypeChecker typeChecker;

    public TypeCheckerProvider(Module module) {
        this.module = module;
    }

    public void initComponent() {
    }

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
        if (FacetManager.getInstance(module).getFacetByType(CeylonFacet.ID) == null) {
            return;
        }

        // If the project was just created, module files (*.iml) do not yet exist. Since we need them, we defer
        // the type checker creation after the project has been loaded
        StartupManager.getInstance(module.getProject()).registerPostStartupActivity(new Runnable() {
            @Override
            public void run() {
                typecheck();
            }
        });
    }

    public void typecheck() {
        if (typeChecker == null) {
            typeChecker = createTypeChecker();
        }
        DaemonCodeAnalyzer.getInstance(module.getProject()).restart();
    }

    public void projectClosed() {
        // called when project is being closed
        typeChecker = null;
    }

    @Override
    public void moduleAdded() {
        System.out.println("TypeCheckerprovider.moduleAdded()");
    }

    public TypeChecker createTypeChecker() {
        IdeaCeylonProjects ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
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
        System.out.println("Getting type checker");
        TypeChecker checker = builder.getTypeChecker();
        System.out.println("Got type checker in " + (System.currentTimeMillis() - startTime) + "ms");

        startTime = System.currentTimeMillis();
        checker.process();
        System.out.println("Type checker process()ed in " + (System.currentTimeMillis() - startTime) + "ms");

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
