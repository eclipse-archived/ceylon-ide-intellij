package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

/**
 *
 */
public class TypeCheckerProvider implements ProjectComponent {

    private final Project project;
    private TypeChecker typeChecker;

    public TypeCheckerProvider(Project project) {
        this.project = project;
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
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    public void projectClosed() {
        // called when project is being closed
        typeChecker = null;
    }

    public TypeChecker createTypeChecker() {
        TypeCheckerBuilder builder = new TypeCheckerBuilder()
                .verbose(false)
                .usageWarnings(true);

        String systemRepoPath = null; // TODO add global settings to configure an existing installation/repo

        RepositoryManager manager = repoManager()
                .cwd(new File(project.getBasePath()))
                .offline(false)
                .systemRepo(systemRepoPath)
                .isJDKIncluded(true)
                .buildManager();
        builder.setRepositoryManager(manager);

        builder.moduleManagerFactory(new ModuleManagerFactory() {
            @Override
            public ModuleManager createModuleManager(final Context context) {
                // FIXME use a real LazyModuleManager to remove this hack
                return new ModuleManager(context) {
                    @Override
                    public void addImplicitImports() {
                        Module languageModule = getContext().getModules().getLanguageModule();
                        for(Module m : getContext().getModules().getListOfModules()){
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

        for (VirtualFile sourceRoot : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
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
