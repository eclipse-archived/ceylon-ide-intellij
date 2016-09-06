import com.intellij.openapi.compiler {
    CompileTask,
    CompileContext,
    CompilerMessageCategory
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaCeylonProject
}
import ceylon.interop.java {
    javaClass,
    CeylonIterable
}
import org.intellij.plugins.ceylon.ide.ceylonCode.settings {
    ceylonSettings
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.util.containers {
    MultiMap
}
import ceylon.collection {
    ArrayList,
    HashMap
}
import java.io {
    File,
    Writer,
    PrintWriter
}
import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import com.vasileff.ceylon.dart.compiler {
    compileDart,
    CompilationStatus
}
import com.redhat.ceylon.model.typechecker.model {
    ModuleModel = Module
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import com.redhat.ceylon.cmr.ceylon {
    CeylonUtils
}
import ceylon.language.meta {
    modules
}
import com.redhat.ceylon.common {
    Backend
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import java.lang {
    CharArray,
    JString=String
}

shared Backend dartBackend = Backend.registerBackend("Dart", "dart");

shared class CeylonDartBuilder() satisfies CompileTask {
    shared actual Boolean execute(CompileContext context) {
        if (ceylonSettings.useOutProcessBuild) {
            // apparanetly, we can just ignore this and compile
            //return true;
        }

        value projects = context.project.getComponent(javaClass<IdeaCeylonProjects>()) else null;

        if (!exists projects) {
            return true;
        }

        value filesByModule = MultiMap<Module, VirtualFile>();

        for (vfile in context.compileScope.getFiles(null, true)) {
            value m = context.getModuleByFile(vfile);
            filesByModule.putValue(m, vfile);
        }

        for (m in context.compileScope.affectedModules) {
            assert (is IdeaCeylonProject? project = projects.getProject(m));

            if (exists project, project.compileToDart) {
                value files = filesByModule.get(m);
                value dartFiles = ArrayList<File>();

                value provider = m.getComponent(javaClass<ITypeCheckerProvider>()) else null;
                if (!exists provider) {
                    continue;
                }
                value tc = provider.typeChecker else null;
                if (!exists tc) {
                    continue;
                }

                for (file in files) {
                    if ((file.extension else "") in ["ceylon", "dart"]
                            && isNative(file, context, m, tc, dartBackend)) {
                        dartFiles.add(File(file.path));
                    }
                }

                if (!compile(context, m, dartFiles, project, tc)) {
                    return false;
                }
            }
        }
        return true;
    }

    function messageWriter(CompileContext context) => PrintWriter(object
            extends Writer() {
        close = noop;
        flush = noop;

        shared actual void write(CharArray? cbuf, Integer off, Integer len)
            =>  context.addMessage(
                    CompilerMessageCategory.information,
                    JString(cbuf, off, len).string, null, -1, -1);
    });

    Boolean compile(CompileContext context, Module m, List<File> files,
            IdeaCeylonProject project, TypeChecker tc) {

        context.addMessage(CompilerMessageCategory.information,
            "Compiling module " + m.name + " to Dart", null, -1, -1);

        value sources = ModuleRootManager.getInstance(m).sourceRoots.iterable
                .coalesced.collect((vf) => File(vf.path));

        value writer = messageWriter(context);

        value [_, resultStatus, messages] = compileDart {
            sourceDirectories = sources;
            sourceFiles = files;
            repositoryManager
                // use the provided typechecker's repository manager, which will
                // include the embeddedDist repository
                =   tc?.context?.repositoryManager else
                    (if (exists systemRepository = project.systemRepository)
                    then CeylonUtils.repoManager().systemRepo(systemRepository).buildManager()
                    else null);
            outputRepositoryManager
                =   CeylonUtils.repoManager()
                        .outRepo(context.getModuleOutputDirectory(m)?.canonicalPath)
                        .buildOutputManager();
            standardOutWriter = writer;
            standardErrorWriter = writer;
        };

        for (message in messages) {
            context.addMessage(
                message.warning then CompilerMessageCategory.warning
                                else CompilerMessageCategory.error,
                message.message, null, message.line, -1);
        }

        switch (resultStatus)
        case (CompilationStatus.success) {
            context.addMessage(
                    CompilerMessageCategory.information,
                    "Dart compilation succeeded", null, -1, -1);
            return true;
        }
        case (CompilationStatus.errorTypeChecker | CompilationStatus.errorDartBackend) {
            context.addMessage(
                    CompilerMessageCategory.information,
                    "Dart compilation failed", null, -1, -1);
            return false;
        }
    }

    Boolean isNative(VirtualFile vfile, CompileContext context, Module m, TypeChecker tc, Backend backend) {
        variable VirtualFile? source = null;

        for (root in ModuleRootManager.getInstance(m).sourceRoots) {
            if (VfsUtil.isAncestor(root, vfile, true)) {
                source = root;
                break;
            }
        }

        if (exists s = source,
                exists relativePath = VfsUtil.getRelativePath(vfile, s),
                exists pu = tc.getPhasedUnitFromRelativePath(relativePath)) {

            value mod = pu.\ipackage.\imodule;

            return mod.nativeBackends.none()
                    || mod.nativeBackends.supports(backend);
        }

        return false;
    }
}
