package org.intellij.plugins.ceylon.jps;

import com.intellij.util.PathUtil;
import org.intellij.plugins.ceylon.compiler.CeylonCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        final Map<JpsModule, Boolean> compileForJvm = new HashMap<>();
        for (JpsModule module : chunk.getModules()) {
            JpsCeylonModuleExtension facet = module.getContainer().getChild(JpsCeylonModuleExtension.KIND);

            if (facet != null) {
                compileForJvm.put(module, facet.getProperties().isCompileForJvm());
            } else {
                compileForJvm.put(module, false);
            }
        }

        try {
            final List<String> filesToBuild = new ArrayList<>();

            dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                @Override
                public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
                    if (Boolean.TRUE.equals(compileForJvm.get(target.getModule())) && file.getName().endsWith(".ceylon")) {
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
            "org.jboss.modules",
            "com.github.rjeschke.txtmark",
            "com.redhat.ceylon.compiler.java",
            "com.redhat.ceylon.module-resolver",
            "com.redhat.ceylon.typechecker",
            "com.redhat.ceylon.model",
            "com.redhat.ceylon.common",
            "ceylon.language"
    };

    public void searchArchivesFromRepo(File path, Map<String, File> filesToAddToClasspath) {
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                searchArchivesFromRepo(f, filesToAddToClasspath);
            }
        } else if (path.isFile()) {
            for (String searchedArchive : searchedArchives) {
                if (path.getName().matches("^" + Pattern.quote(searchedArchive) + "-.+\\.(j|c)ar$")) {
                    filesToAddToClasspath.put(searchedArchive, path);
                    break;
                }
            }
        }
    }

    private ExitCode compile(final CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        File pluginClassesDir = new File(PathUtil.getJarPathForClass(CeylonCompiler.class));

        if (pluginClassesDir.isFile()) {
            throw new IOException("'CeylonCompiler' class is within a Jar file while it should be in a 'classes' directory.\n" +
                    "The Ceylon IDEA plugin should have been deployed in 'directory' mode.");
        }

        // Load the required JARs in a separate confined classloader to avoid conflicts with classes from the JDK
        List<URL> classpath = new ArrayList<>();
        classpath.add(pluginClassesDir.toURI().toURL());

        File ceylonRepoDir = new File(pluginClassesDir, "repo");
        if (! ceylonRepoDir.exists()) {
            throw new IOException("The Ceylon IDEA plugin 'classes' directory doesn't contain the embedded Ceylon repository.");
        }

        Map<String, File> filesToAddToClasspath = new HashMap<>();
        searchArchivesFromRepo(ceylonRepoDir, filesToAddToClasspath);
        List<String> missingCeylonArchives = new ArrayList<>();
        for (String searchedArchive : searchedArchives) {
            File archiveToAdd = filesToAddToClasspath.get(searchedArchive);
            if (archiveToAdd == null) {
                missingCeylonArchives.add(searchedArchive);
            } else {
                classpath.add(archiveToAdd.toURI().toURL());
            }
        }

        if (! missingCeylonArchives.isEmpty()) {
            throw new IOException("Ceylon archives " + missingCeylonArchives.toArray().toString() +
                    " required by '" + CeylonBuilder.class.getName() +
                    "' was not found\nin the following Ceylon repository directory : '" + ceylonRepoDir + "'");
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
