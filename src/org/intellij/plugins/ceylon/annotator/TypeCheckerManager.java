package org.intellij.plugins.ceylon.annotator;

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

public class TypeCheckerManager {

    private final Project project;
    private TypeChecker typeChecker;
    private boolean isBuildingTypeChecker = false;

    public TypeCheckerManager(Project project) {
        this.project = project;
    }

    public TypeChecker getTypeChecker() {
        if (typeChecker == null && !isBuildingTypeChecker) {
            isBuildingTypeChecker = true; // TODO really thread-safe?

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Preparing Ceylon project...") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            typeChecker = createTypeChecker();
                            isBuildingTypeChecker = false;
                            DaemonCodeAnalyzer.getInstance(project).restart();
                        }
                    });
                }
            });
        }

        return typeChecker;
    }

    public TypeChecker createTypeChecker() {
        TypeCheckerBuilder builder = new TypeCheckerBuilder()
                .verbose(false)
                .usageWarnings(true);

        String systemRepoPath = null; // TODO add global settings to configure an existing installation/repo

        RepositoryManager manager = repoManager()
                .cwd(new File(project.getBasePath()))
                .offline(true)
                .systemRepo(systemRepoPath)
                .isJDKIncluded(true)
                .buildManager();
        builder.setRepositoryManager(manager);

        for (VirtualFile sourceRoot : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            builder.addSrcDirectory(new VFileAdapter(sourceRoot));
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
