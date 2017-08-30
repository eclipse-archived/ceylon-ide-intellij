import ceylon.language {
    Set
}

import com.intellij.compiler.server {
    BuildManager
}
import com.intellij.openapi.compiler {
    CompileContext,
    CompileTask
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.util.containers {
    MultiMap
}
import com.redhat.ceylon.common {
    Backend
}
import com.redhat.ceylon.ide.common.model {
    CeylonProjectBuild
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile
}

import java.io {
    File,
    IOException
}
import java.lang {
    Str=String,
    Types {
        str=nativeString
    }
}
import java.nio.charset {
    StandardCharsets
}
import java.nio.file {
    Files,
    StandardOpenOption
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        withUpToDateIndexes
    },
    IdeaCeylonProject,
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.model.parsing {
    ProgressIndicatorMonitor
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}
import org.intellij.plugins.ceylon.ide.vfs {
    VirtualFileVirtualFile
}
shared class CeylonBuilder() satisfies CompileTask {

    shared actual Boolean execute(CompileContext compileContext) {
        value projects = compileContext.project.getComponent(`IdeaCeylonProjects`);
        variable value result = true;
        if (exists projects) {
            value builderDir = BuildManager.instance.getProjectSystemDirectory(compileContext.project);
            if (exists builderDir) {
                value files = builderDir.listFiles();
                if (exists files) {
                    for (file in files) {
                        if (file.file && file.name.startsWith("ceylonFiles-")) {
                            file.delete();
                        }
                    }
                }
            }
            for (mod in compileContext.compileScope.affectedModules) {
                if (is IdeaCeylonProject project = projects.getProject(mod),
                    project.compileToJava || project.compileToJs) {
                    value monitor = ProgressIndicatorMonitor.wrap(compileContext.progressIndicator);
                    value res = withUpToDateIndexes(() {
                        project.build.performBuild(monitor);
                        if (ceylonSettings.useOutProcessBuild) {
                            project.build.performBinaryGeneration(monitor, project.build.binaryGenerator(
                                (CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile> build,
                                 Set<FileVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>> files) {
                                    registerFilesToCompile(compileContext.project, files);
                                    return true;
                                }));
                        } else {
                            // TODO default binary generation
                        }
                        return true;
                    });
                    result &&= res==true;
                }
            }
        }
        return result;
    }

    void registerFilesToCompile(Project project,
            Set<FileVirtualFile<Module, VirtualFile, VirtualFile, VirtualFile>> filesToBuild) {
        value files = MultiMap<Backend,Str>();
        for (vfile in filesToBuild) {
            if (is VirtualFileVirtualFile vfile) {
                value file = File(vfile.nativeResource.canonicalPath);
                if (file.\iexists()) {
                    assert (exists cm = vfile.ceylonModule);
                    if (cm.nativeBackends.none()) {
                        files.putValue(Backend.java, str(file.absolutePath));
                        files.putValue(Backend.javaScript, str(file.absolutePath));
                    }
                    else {
                        for (backend in cm.nativeBackends) {
                            files.putValue(backend, str(file.absolutePath));
                        }
                    }
                }
            }
        }
        value builderDir = BuildManager.instance.getProjectSystemDirectory(project);
        try {
            for (entry in files.entrySet()) {
                value ceylonFiles = File(builderDir, "ceylonFiles-``entry.key.name``.txt");
                Files.write(ceylonFiles.toPath(), entry.\ivalue,
                    StandardCharsets.utf8, StandardOpenOption.create,
                    StandardOpenOption.append, StandardOpenOption.write);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
