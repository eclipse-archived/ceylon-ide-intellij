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
import com.intellij.util.containers.MultiMap;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing.ProgressIndicatorMonitor;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs.VirtualFileVirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

public class CeylonBuilder implements CompileTask {

    @Override
    public boolean execute(final CompileContext compileContext) {
        IdeaCeylonProjects projects = compileContext.getProject()
                .getComponent(IdeaCeylonProjects.class);

        boolean result = true;

        if (projects != null) {
            File builderDir = BuildManager.getInstance().getProjectSystemDirectory(compileContext.getProject());
            if (builderDir != null) {
                File[] files = builderDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().startsWith("ceylonFiles-")) {
                            file.delete();
                        }
                    }
                }
            }

            for (Module mod : compileContext.getCompileScope().getAffectedModules()) {
                final IdeaCeylonProject project = (IdeaCeylonProject) projects.getProject(mod);

                if (project != null && (project.getCompileToJava() || project.getCompileToJs())) {
                    final ProgressIndicatorMonitor monitor = new ProgressIndicatorMonitor(
                            ProgressIndicatorMonitor.wrap_,
                            compileContext.getProgressIndicator()
                    );

                    result &= (Boolean) ConcurrencyManagerForJava.withUpToDateIndexes(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            project.getBuild().performBuild(monitor);
                            if (ceylonSettings_.get_().getUseOutProcessBuild()) {
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
                            return Boolean.TRUE;
                        }
                    });
                }
            }
        }
        return result;
    }

    private void registerFilesToCompile(Project project,
                                        Set<? extends FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile>> filesToBuild) {
        MultiMap<Backend, String> files = new MultiMap<>();
        Iterator it = filesToBuild.iterator();

        while (true) {
            Object next = it.next();

            if (next == finished_.get_()) {
                break;
            } else if (next instanceof VirtualFileVirtualFile) {
                VirtualFileVirtualFile vfile = (VirtualFileVirtualFile) next;
                File file = new File(vfile.getNativeResource().getCanonicalPath());

                if (file.exists()) {
                    if (vfile.getCeylonModule().getNativeBackends().none()) {
                        files.putValue(Backend.Java, file.getAbsolutePath());
                        files.putValue(Backend.JavaScript, file.getAbsolutePath());
                    } else {
                        for (Backend backend : vfile.getCeylonModule().getNativeBackends()) {
                            files.putValue(backend, file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        File builderDir = BuildManager.getInstance().getProjectSystemDirectory(project);
        try {
            for (Map.Entry<Backend, Collection<String>> entry : files.entrySet()) {
                File ceylonFiles = new File(builderDir, "ceylonFiles-" + entry.getKey().name + ".txt");
                Files.write(ceylonFiles.toPath(), entry.getValue(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
