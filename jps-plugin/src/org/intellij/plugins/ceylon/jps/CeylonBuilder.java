package org.intellij.plugins.ceylon.jps;

import com.intellij.util.ArrayUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.messages.ProgressMessage;
import org.jetbrains.jps.model.java.JpsJavaSdkType;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.service.SharedThreadPool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 */
public class CeylonBuilder extends ModuleLevelBuilder {

    public static final Pattern ERROR_PATTERN = Pattern.compile("(.*):(\\d+): error: (.*)");
    private static final String BUILDER_NAME = "ceylon";

    protected CeylonBuilder() {
        super(BuilderCategory.TRANSLATOR);
    }

    /**
     * Returns the path to the Java executable.
     * @param chunk
     * @return the path to the Java executable
     */
    private static String getJavaExecutable(ModuleChunk chunk) {
        JpsSdk<?> sdk = chunk.getModules().iterator().next().getSdk(JpsJavaSdkType.INSTANCE);
        // TODO use SDK's JDK
        // TODO also enable JS compilation
//        if (sdk != null) {
//            return JpsJavaSdkType.getJavaExecutable(sdk);
//        }
//        return SystemProperties.getJavaHome() + "/bin/java";
        return "java";
    }

    @Override
    public ExitCode build(final CompileContext context, ModuleChunk chunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        try {
            final List<String> filesToBuild = new ArrayList<>();

            dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                @Override
                public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
                    if (file.getName().endsWith(".ceylon")) {
                        String path = file.getAbsolutePath();
                        filesToBuild.add(path);
                    }
                    return true;
                }
            });

            return compile(context, chunk, filesToBuild);
        } catch (Exception e) {
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, "Could not launch compiler"));
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, e.getMessage()));
            e.printStackTrace();
            return ExitCode.ABORT;
        }
    }

    private ExitCode compile(final CompileContext context, ModuleChunk chunk, List<String> filesToBuild) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        List<String> programParams = ContainerUtilRt.newArrayList();

        programParams.add(getJavaExecutable(chunk));
        programParams.add("-jar");
        programParams.add(chunk.getModules().iterator().next().getSdk(JpsJavaSdkType.INSTANCE).getHomePath() + "/lib/ceylon-bootstrap.jar");
        programParams.add("compile");
        programParams.add("--out");
        programParams.add(chunk.getTargets().iterator().next().getOutputDir().getAbsolutePath());
        programParams.add("--rep");
        programParams.add("http://modules.ceylon-lang.org/test/");

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                programParams.add("--src=" + sourceRoot.getFile().getAbsolutePath());
            }
        }

        programParams.addAll(filesToBuild);

        final Process process = Runtime.getRuntime().exec(ArrayUtil.toStringArray(programParams));

        final Consumer<String> updater = new Consumer<String>() {
            public void consume(String s) {
                context.processMessage(new ProgressMessage(s));
            }
        };
        final CeylonProcessHandler handler = new CeylonProcessHandler(process, updater) {
            @Override
            protected Future<?> executeOnPooledThread(Runnable task) {
                return SharedThreadPool.getInstance().executeOnPooledThread(task);
            }
        };

        handler.startNotify();
        handler.waitFor();

        String stdErr = handler.getStdErr();

        if (!"".equals(stdErr)) {
            reportErrorsIfAny(context, stdErr);
        }
        return ExitCode.OK;
    }

    private void reportErrorsIfAny(CompileContext context, String rawOutput) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(rawOutput));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            Matcher matcher = ERROR_PATTERN.matcher(line);
            if (matcher.matches()) {
                reader.readLine();
                line = reader.readLine();
                int column = 0;
                if (line != null) {
                    column = line.indexOf('^') + 1;
                }
                context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, matcher.group(3),
                        matcher.group(1), 0, 0, 0, Integer.parseInt(matcher.group(2)), column));
            } else {
                context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO, line));
            }
        }
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Ceylon compiler";
    }
}
