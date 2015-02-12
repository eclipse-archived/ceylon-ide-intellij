package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.ide.common.CeylonProject;
import com.redhat.ceylon.ide.common.CeylonProjectConfig;
import org.intellij.plugins.ceylon.ide.IdePluginCeylonStartup;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

/**
 *
 */
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
        // called when project is opened
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


        String systemRepo = IdePluginCeylonStartup.getEmbeddedCeylonRepository().getAbsolutePath();
        if (systemRepo == null) {
            systemRepo = ceylonConfig.getProjectRepositories().getSystemRepoDir().getAbsolutePath();
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
                return new ModuleManager(context) {
                    @Override
                    public void addImplicitImports() {
                        com.redhat.ceylon.compiler.typechecker.model.Module languageModule = getContext().getModules().getLanguageModule();
                        for(com.redhat.ceylon.compiler.typechecker.model.Module m : getContext().getModules().getListOfModules()){
                            // Java modules don't depend on ceylon.language
                            if((!(m instanceof LazyModule) || !m.isJava()) && !m.equals(languageModule)) {
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

}
