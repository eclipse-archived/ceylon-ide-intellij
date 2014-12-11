package org.intellij.plugins.ceylon.jps;

import com.intellij.util.PathUtil;
import com.redhat.ceylon.common.Versions;
import org.intellij.plugins.ceylon.compiler.CeylonCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class CeylonBuilder extends ModuleLevelBuilder {

    public static final String BUILDER_NAME = "ceylon";

    protected CeylonBuilder() {
        super(BuilderCategory.TRANSLATOR);
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

    private ExitCode compile(final CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        // Required JARs should be located in the same folder as common.jar
        File pathToLibs = new File(PathUtil.getJarPathForClass(Versions.class));

        if (pathToLibs.isFile()) {
            pathToLibs = pathToLibs.getParentFile();
        }

        // Load the required JARs in a separate confined classloader to avoid conflicts with classes from the JDK
        // TODO could we use JARs from defaultRepository instead?
        URL[] classpath = new URL[]{
                new File(PathUtil.getJarPathForClass(CeylonCompiler.class)).toURI().toURL(),
                new File(pathToLibs, "org.antlr.runtime-3.4.jar").toURI().toURL(),
                new File(pathToLibs, "com.redhat.ceylon.compiler.java-1.1.0.jar").toURI().toURL(),
                new File(pathToLibs, "com.redhat.ceylon.module-resolver-1.1.0.jar").toURI().toURL(),
                new File(pathToLibs, "com.redhat.ceylon.typechecker-1.1.0.jar").toURI().toURL(),
                new File(pathToLibs, "com.github.rjeschke.txtmark-0.11").toURI().toURL(),
                new File(pathToLibs, "org.jboss.jandex-1.0.3.Final.jar").toURI().toURL()
        };

        ChildFirstURLClassLoader loader = new ChildFirstURLClassLoader(classpath, getClass().getClassLoader(),
                new String[]{"com.sun.source", "com.sun.tools.javac", "org.intellij.plugins.ceylon.compiler"},
                new String[]{"com.sun.tools.javac.resources.compiler"}
        );

        try {
            Class<?> compilerClass = Class.forName("org.intellij.plugins.ceylon.compiler.CeylonCompiler", true, loader);
            Object compiler = compilerClass.newInstance();
            compilerClass.getMethod("compile", CompileContext.class, ModuleChunk.class, List.class).invoke(compiler, ctx, chunk, filesToBuild);
        } catch (ReflectiveOperationException e) {
            ctx.processMessage(new CompilerMessage(BUILDER_NAME, e));
            return ExitCode.ABORT;
        }

        return ExitCode.OK;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Ceylon compiler";
    }
}
