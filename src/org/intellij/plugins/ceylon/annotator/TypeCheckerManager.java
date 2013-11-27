package org.intellij.plugins.ceylon.annotator;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonProgressIndicator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.changes.RunnableBackgroundableWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import org.intellij.plugins.ceylon.sdk.CeylonSdk;

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

            Runnable instantiateTypeCheckerTask = new Runnable() {
                @Override
                public void run() {
                    typeChecker = createTypeChecker();
                    isBuildingTypeChecker = false;
                    DaemonCodeAnalyzer.getInstance(project).restart();
                }
            };

            RunnableBackgroundableWrapper taskWrapper = new RunnableBackgroundableWrapper(project, "Preparing Ceylon project", instantiateTypeCheckerTask);
            ProgressIndicator progressIndicator = new BackgroundableProcessIndicator(taskWrapper);
            progressIndicator.setIndeterminate(true);
            progressIndicator.setText("Preparing Ceylon project...");

            // We still get a (harmless?) error :(
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(taskWrapper, progressIndicator);
        }

        return typeChecker;
    }

    private TypeChecker createTypeChecker() {
        TypeCheckerBuilder builder = new TypeCheckerBuilder()
                .verbose(false)
                .usageWarnings(true);

        String systemRepoPath = null;
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if (projectSdk != null) {
            SdkTypeId sdkType = projectSdk.getSdkType();

            if (sdkType instanceof CeylonSdk) {
                systemRepoPath = projectSdk.getHomeDirectory().findChild("repo").getCanonicalPath();
            }
        }

        RepositoryManager manager = repoManager()
                .cwd(new File(project.getBasePath()))
                .offline(true)
                .systemRepo(systemRepoPath)
                .isJDKIncluded(true)
                .buildManager();
        builder.setRepositoryManager(manager);

        for (VirtualFile sourceRoot : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            builder.addSrcDirectory(new File(sourceRoot.getPath()));
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
