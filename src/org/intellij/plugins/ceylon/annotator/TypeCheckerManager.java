package org.intellij.plugins.ceylon.annotator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;

import java.io.File;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;

public class TypeCheckerManager {

    private final Project project;
    private TypeChecker typeChecker;

    public TypeCheckerManager(Project project) {
        this.project = project;
    }

    public TypeChecker getTypeChecker() {
        if (typeChecker == null) {
            typeChecker = createTypeChecker();
        }

        return typeChecker;
    }

    private TypeChecker createTypeChecker() {
        TypeCheckerBuilder builder = new TypeCheckerBuilder();

        RepositoryManager manager = repoManager()
                .cwd(new File(project.getBasePath()))
                .systemRepo("/Users/bastien/Dev/ceylon/ceylon-0.4/repo")
                .buildManager();
        builder.setRepositoryManager(manager);

        for (VirtualFile sourceRoot : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            builder.addSrcDirectory(new File(sourceRoot.getPath()));
        }

        builder.verbose(true);
        TypeChecker checker = builder.getTypeChecker();
        checker.process();

        return checker;
    }
}
