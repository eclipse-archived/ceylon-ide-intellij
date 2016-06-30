package org.intellij.plugins.ceylon.ide.build;

import ceylon.language.Iterator;
import ceylon.language.Set;
import ceylon.language.finished_;
import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing.ProgressIndicatorMonitor;
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs.VirtualFileVirtualFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.CeylonSettings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CeylonBuilder implements CompileTask {

    @Override
    public boolean execute(final CompileContext compileContext) {
        IdeaCeylonProjects projects = compileContext.getProject()
                .getComponent(IdeaCeylonProjects.class);

        if (projects != null) {
            for (Module mod : compileContext.getCompileScope().getAffectedModules()) {
                final IdeaCeylonProject project = (IdeaCeylonProject) projects.getProject(mod);

                if (project != null && project.getCompileToJava()) {
                    ProgressIndicatorMonitor monitor = new ProgressIndicatorMonitor(
                            ProgressIndicatorMonitor.wrap_,
                            compileContext.getProgressIndicator()
                    );

                    project.getBuild().performBuild(monitor);

                    if (CeylonSettings.getInstance().isUseOutProcessBuild()) {
                        project.getBuild().performBinaryGeneration(monitor, project.getBuild().new BinaryGenerator() {
                            @Override
                            public boolean build(CeylonProjectBuild<Module, VirtualFile, VirtualFile, VirtualFile> build,
                                                 Set<? extends FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile>> files) {
                                registerFilesToCompile(compileContext.getProject(), files);
                                return true;
                            }
                        });
                    } else {
                        // TODO default binary generation
                    }
                }
            }
        }
        return true;
    }

    private void registerFilesToCompile(Project project,
                                        Set<? extends FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile>> filesToBuild) {
        List<String> files = new ArrayList<>();
        Iterator it = filesToBuild.iterator();

        while (true) {
            Object next = it.next();

            if (next == finished_.get_()) {
                break;
            } else if (next instanceof VirtualFileVirtualFile){
                VirtualFile vfile = ((VirtualFileVirtualFile) next).getNativeResource();
                File file = new File(vfile.getCanonicalPath());

                if (file.exists()) {
                    files.add(file.getAbsolutePath());
                }
            }
        }

        File builderDir = BuildManager.getInstance().getProjectSystemDirectory(project);
        File ceylonFiles = new File(builderDir, "ceylonFiles.txt");
        try {
            Files.write(ceylonFiles.toPath(), files, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
