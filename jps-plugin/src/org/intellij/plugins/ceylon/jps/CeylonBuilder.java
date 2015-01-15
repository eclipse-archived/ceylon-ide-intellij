package org.intellij.plugins.ceylon.jps;

import com.intellij.util.PathUtil;
import com.redhat.ceylon.launcher.Launcher;
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
import java.util.regex.Pattern;

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

    private static final String[] searchedArchives = {
            "org.antlr.runtime",
            "org.jboss.jandex",
            "com.github.rjeschke.txtmark",
            "com.redhat.ceylon.compiler.java",
            "com.redhat.ceylon.module-resolver",
            "com.redhat.ceylon.typechecker",
            "com.redhat.ceylon.common"
    };

    private ExitCode compile(final CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        // Required JARs should be located in the same folder as common.jar
        File pathToLibs = new File(PathUtil.getJarPathForClass(Launcher.class));

        if (pathToLibs.isFile()) {
            pathToLibs = pathToLibs.getParentFile();
        }

        // Load the required JARs in a separate confined classloader to avoid conflicts with classes from the JDK
        List<URL> classpath = new ArrayList<>();
        classpath.add(new File(PathUtil.getJarPathForClass(CeylonCompiler.class)).toURI().toURL());

        for (String searchedArchive : searchedArchives) {
            File archiveToAdd = null;
            for (File archive : pathToLibs.listFiles()) {
                if (archive.getName().matches("^" + Pattern.quote(searchedArchive) + "-.+\\.jar$")) {
                    archiveToAdd = archive;
                    break;
                }
            }
            if (archiveToAdd == null) {
                new IOException("Ceylon archive " + searchedArchive +
                        " required by '" + CeylonBuilder.class.getName() +
                        "' was not found in directory '" + pathToLibs + "'");
            }
            classpath.add(archiveToAdd.toURI().toURL());
        }

        ChildFirstURLClassLoader loader = new ChildFirstURLClassLoader(classpath.toArray(new URL[0]), getClass().getClassLoader(),
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
