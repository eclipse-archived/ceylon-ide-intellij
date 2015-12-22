package org.intellij.plugins.ceylon.compiler;

import com.intellij.openapi.application.PathManager;
import com.intellij.util.PathUtil;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.java.launcher.Main;
import com.redhat.ceylon.compiler.java.tools.CeylonLog;
import com.redhat.ceylon.compiler.java.tools.CeyloncTaskImpl;
import com.redhat.ceylon.compiler.java.tools.CeyloncTool;
import com.redhat.ceylon.langtools.source.util.TaskEvent;
import com.redhat.ceylon.langtools.source.util.TaskListener;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.javac.PlainMessageDiagnostic;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;

import com.redhat.ceylon.javax.tools.Diagnostic;
import com.redhat.ceylon.javax.tools.DiagnosticListener;
import com.redhat.ceylon.javax.tools.JavaFileObject;
import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO
 */
@SuppressWarnings("UnusedDeclaration")
public class CeylonCompiler {

    public void compile(final CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) throws MalformedURLException {
        PrintWriter printWriter = new PrintWriter(System.out);
        List<String> options = buildOptions(chunk);

        CeyloncTool compiler;
        try {
            compiler = new CeyloncTool();
        } catch (VerifyError e) {
            System.err.println("ERROR: Cannot run tests! Did you maybe forget to configure the -Xbootclasspath/p: parameter?");
            throw new RuntimeException(e);
        }

        CompileErrorReporter errorReporter = new CompileErrorReporter(ctx);

        final com.redhat.ceylon.langtools.tools.javac.util.Context context = new com.redhat.ceylon.langtools.tools.javac.util.Context();
        context.put(com.redhat.ceylon.langtools.tools.javac.util.Log.outKey, printWriter);
        context.put(DiagnosticListener.class, errorReporter);
        CeylonLog.preRegister(context);

        BuildFileManager fileManager = new BuildFileManager(context, true, null, ctx);

        // TODO
//        computeCompilerClasspath(project, javaProject, options);

        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromStrings(filesToBuild);

        CeyloncTaskImpl task = compiler.getTask(printWriter,
                fileManager, errorReporter, options, null,
                compilationUnits);

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
            errorReporter.report(new Diagnostic<JavaFileObject>() {
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
            });
            errorReporter.failed();
        }
    }

    /**
     * Creates a list of options to pass to CeyloncTool.
     *
     * @param chunk the module chunk being built
     * @return a list of options for CeyloncTool
     */
    private List<String> buildOptions(ModuleChunk chunk) {
        List<String> options = new ArrayList<>();

        StringBuilder srcPath = new StringBuilder();

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                if (!(srcPath.length() == 0)) {
                    srcPath.append(File.pathSeparator);
                }
                srcPath.append(sourceRoot.getFile().getAbsolutePath());
            }
        }

        options.add("-src");
        options.add(srcPath.toString());

        options.add("-g:lines,vars,source");

        File outputDir = chunk.representativeTarget().getOutputDir();
        if (outputDir != null) {
            options.add("-out");
            options.add(outputDir.getAbsolutePath());
        } else {
            throw new IllegalArgumentException("Can't detect compiler output path");
        }

        options.add("-sysrep"); // FIXME should take the repo configured in the facet, if present
        options.add(getSystemRepoPath());

        String pathToCeylonc = PathManager.getJarPathForClass(CeyloncTool.class);
        File pathToJars = new File(pathToCeylonc).getParentFile();

        options.add("-classpath");
        options.add(new File(pathToJars, "ceylon.language-" + Versions.CEYLON_VERSION_NUMBER + ".car").getAbsolutePath());

        return options;
    }

    private String getSystemRepoPath() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(CeylonCompiler.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(pluginClassesDir, "repo");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir.getAbsolutePath();
            }
        }

        throw new RuntimeException("Embedded Ceylon system repo not found");
    }
}
