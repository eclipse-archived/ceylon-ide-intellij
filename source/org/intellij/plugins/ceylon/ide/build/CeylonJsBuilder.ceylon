import com.intellij.openapi.compiler {
    CompileContext,
    CompileTask,
    CompilerMessageCategory {
        ...
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import com.intellij.openapi.vfs {
    VfsUtil,
    VirtualFile
}
import com.intellij.util.containers {
    MultiMap
}
import com.redhat.ceylon.common {
    Backend
}
import com.redhat.ceylon.compiler.js {
    JsCompiler
}
import com.redhat.ceylon.compiler.js.util {
    Options
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}

import java.io {
    File,
    IOException
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}

shared class CeylonJsBuilder() satisfies CompileTask {

    shared actual Boolean execute(CompileContext context) {
        if (ceylonSettings.useOutProcessBuild) {
            return true;
        }
        value projects = context.project.getComponent(`IdeaCeylonProjects`);
        if (!exists projects) {
            return true;
        }
        value filesByModule = MultiMap<Module,VirtualFile>();
        for (vfile in context.compileScope.getFiles(null, true)) {
            value mod = context.getModuleByFile(vfile);
            filesByModule.putValue(mod, vfile);
        }
        for (mod in context.compileScope.affectedModules) {
            if (is IdeaCeylonProject project = projects.getProject(mod),
                project.compileToJs) {
                value files = filesByModule.get(mod);
                value jsFiles = ArrayList<File>();
                value tc = project.typechecker;
                if (!exists tc) {
                    continue;
                }
                for (file in files) {
                    if (exists ext = file.extension,
                        ext=="ceylon" || ext=="js",
                        isNative(file, context, mod, tc, Backend.javaScript)) {
                        jsFiles.add(File(file.path));
                    }
                }
                if (!compileJs(context, mod, jsFiles, project, tc)) {
                    return false;
                }
            }
        }
        return true;
    }

    Boolean compileJs(CompileContext context, Module mod, List<File> files, IdeaCeylonProject project, TypeChecker tc) {
        context.addMessage(information, "Compiling module ``mod.name`` to JS", null, -1, -1);
        value sources = ArrayList<File>();
        for (source in ModuleRootManager.getInstance(mod).sourceRoots) {
            sources.add(File(source.path));
        }
        value options
                = Options()
                    .outWriter(MessageWriter(context))
                    .sourceDirs(sources)
                    .systemRepo(project.systemRepository)
                    .outRepo(context.getModuleOutputDirectory(mod)?.canonicalPath)
                    .optimize(true)
                    .generateSourceArchive(true)
                    .verbose(ceylonSettings.verbosityLevel);
        value compiler
                = JsCompiler(tc, options)
                    .stopOnErrors(false)
                    .setSourceFiles(files);

        try {
            if (!compiler.generate()) {
                for (message in compiler.errors) {
                    context.addMessage(error, message.message, null, message.line, -1);
                }
                return false;
            }
            context.addMessage(information, "JS compilation succeeded", null, -1, -1);
        }
        catch (IOException e) {
            context.addMessage(error, e.message, null, -1, -1);
            return false;
        }
        return true;
    }

    Boolean isNative(VirtualFile vfile, CompileContext context, Module mod, TypeChecker tc, Backend backend) {
        for (root in ModuleRootManager.getInstance(mod).sourceRoots) {
            if (VfsUtil.isAncestor(root, vfile, true)) {
                value relativePath = VfsUtil.getRelativePath(vfile, root);
                if (exists pu = tc.getPhasedUnitFromRelativePath(relativePath)) {
                    value m = pu.\ipackage.\imodule;
                    return m.nativeBackends.none()
                        || m.nativeBackends.supports(backend);
                }
                break;
            }
        }
        return false;
    }

}
