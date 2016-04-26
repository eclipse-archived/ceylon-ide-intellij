package org.intellij.plugins.ceylon.compiler;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.common.tools.ModuleWildcardsHelper;
import com.redhat.ceylon.compiler.java.launcher.Main;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.CeyloncTool;
import com.redhat.ceylon.javax.tools.Diagnostic;
import com.redhat.ceylon.javax.tools.DiagnosticListener;
import com.redhat.ceylon.javax.tools.JavaFileObject;
import com.redhat.ceylon.langtools.source.util.TaskEvent;
import com.redhat.ceylon.langtools.source.util.TaskListener;
import com.redhat.ceylon.langtools.tools.javac.util.Context;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnusedDeclaration")
public class CeylonCompiler {

    public void compile(final CompileContext ctx,
                        ModuleChunk chunk,
                        List<String> filesToBuild,
                        List<String> options)
            throws MalformedURLException {

        PrintWriter printWriter = new PrintWriter(System.out);

        CeyloncTool compiler;
        try {
            compiler = new CeyloncTool();
        } catch (VerifyError e) {
            System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
            throw new RuntimeException(e);
        }

        CompileErrorReporter errorReporter = new CompileErrorReporter(ctx);

        final Context context = new Context();
        context.put(com.redhat.ceylon.langtools.tools.javac.util.Log.outKey, printWriter);
        context.put(DiagnosticListener.class, errorReporter);
        CeylonLog.preRegister(context);

        BuildFileManager fileManager = new BuildFileManager(context, true, null, ctx);

        // TODO
//        computeCompilerClasspath(project, javaProject, options);

//        Iterable<? extends JavaFileObject> compilationUnits =
//                fileManager.getJavaFileObjectsFromStrings(filesToBuild);
        List<String> classnames = ModuleWildcardsHelper.expandWildcards(getSourceRoots(chunk),
                Collections.singletonList("*"), Backend.Java);

        CeyloncTaskImpl task = compiler.getTask(printWriter,
                fileManager, errorReporter, options, classnames,
                Collections.<JavaFileObject>emptyList());

        task.setTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent ta) {
                String name = ta.getSourceFile().getName();
                name = name.substring(name.lastIndexOf("/") + 1);
                ctx.processMessage(new ProgressMessage("Compiling " + name));
            }

            @Override
            public void finished(TaskEvent ta) {
            }
        });

        boolean success = false;
        try {
            success = task.call();
        } catch (Exception e) {
            e.printStackTrace(printWriter);
        }
        if (!success) {
            final Main.ExitState exitState = task.getExitState();
            errorReporter.report(new JavaFileObjectDiagnostic(exitState));
            errorReporter.failed();
        }
    }

    List<File> getSourceRoots(ModuleChunk chunk) {
        List<File> roots = new ArrayList<>();

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                roots.add(sourceRoot.getFile());
            }
        }

        return roots;
    }

    private static class JavaFileObjectDiagnostic implements Diagnostic<JavaFileObject> {
        private final Main.ExitState exitState;

        JavaFileObjectDiagnostic(Main.ExitState exitState) {
            this.exitState = exitState;
        }

        @Override
        public Kind getKind() {
            return Kind.ERROR;
        }

        @Override
        public JavaFileObject getSource() {
            return null;
        }

        @Override
        public long getPosition() {
            return 0;
        }

        @Override
        public long getStartPosition() {
            return 0;
        }

        @Override
        public long getEndPosition() {
            return 0;
        }

        @Override
        public long getLineNumber() {
            return 0;
        }

        @Override
        public long getColumnNumber() {
            return 0;
        }

        @Override
        public String getCode() {
            return null;
        }

        @Override
        public String getMessage(Locale locale) {
            return exitState.abortingException == null ? "Unknown error" : exitState.abortingException.toString();
        }
    }
}
