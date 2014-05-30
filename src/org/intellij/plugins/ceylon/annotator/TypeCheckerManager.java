package org.intellij.plugins.ceylon.annotator;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.SettableFuture;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TypeCheckerManager {

    private final Project project;

    private final Supplier<TypeChecker> typeCheckerSupplier = Suppliers.memoize(
            new Supplier<TypeChecker>() {
                @Override
                public TypeChecker get() {
                    final SettableFuture<TypeChecker> resultSetter = SettableFuture.create();
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ProgressManager.getInstance().run(new Task.Modal(project, "Preparing Ceylon project...", false) {
                                @Override
                                public void run(@NotNull ProgressIndicator indicator) {
                                    resultSetter.set(createTypeChecker());
                                    DaemonCodeAnalyzer.getInstance(project).restart();
                                }
                            });
                        }
                    });
                    try {
                        return resultSetter.get(15, SECONDS);
                    } catch (Exception e) {
                        System.out.println("Unable to get a Ceylon TypeChecker! " + e);
                        return null;
                    }
                }
            });

    public TypeCheckerManager(Project project) {
        this.project = project;
    }

    public TypeChecker getTypeChecker() {
        return typeCheckerSupplier.get();
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
