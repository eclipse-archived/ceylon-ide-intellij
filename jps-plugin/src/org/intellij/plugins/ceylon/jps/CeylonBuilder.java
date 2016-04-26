package org.intellij.plugins.ceylon.jps;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import org.intellij.plugins.ceylon.compiler.CeylonCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.fs.FilesDelta;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

class CeylonBuilder extends ModuleLevelBuilder {

    private static final String BUILDER_NAME = "ceylon";

    CeylonBuilder() {
        // We need to be called before the Java compiler
        super(BuilderCategory.SOURCE_PROCESSOR);
    }

    @Override
    public List<String> getCompilableFileExtensions() {
        return Arrays.asList("java", "ceylon");
    }

    @Override
    public ExitCode build(final CompileContext context, ModuleChunk chunk,
                          DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                          OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        final Map<JpsModule, Boolean> compileForJvm = new HashMap<>();
        for (JpsModule module : chunk.getModules()) {
            JpsCeylonModuleExtension facet = module.getContainer().getChild(JpsCeylonModuleExtension.KIND);

            if (facet != null) {
                compileForJvm.put(module, facet.getProperties().isCompileForJvm());
            } else {
                compileForJvm.put(module, false);
            }
        }

        if (compileForJvm.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        try {
            final List<String> filesToBuild = new ArrayList<>();

            dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                @Override
                public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
                    if (Boolean.TRUE.equals(compileForJvm.get(target.getModule()))
                            && (file.getName().endsWith(".ceylon") || file.getName().endsWith(".java"))) {
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

    /**
     * Creates a list of options to pass to CeyloncTool.
     *
     * @param chunk the module chunk being built
     * @return a list of options for CeyloncTool
     */
    private List<String> buildOptions(ModuleChunk chunk) {
        List<String> options = new ArrayList<>();

        StringBuilder srcPath = new StringBuilder();
        StringBuilder resourcePath = new StringBuilder();

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                JpsModuleSourceRootType<?> expectedSrcRootType;
                JpsModuleSourceRootType<?> expectedResourceRootType;

                if (chunk.containsTests()) {
                    expectedSrcRootType = JavaSourceRootType.SOURCE;
                    expectedResourceRootType = JavaResourceRootType.RESOURCE;
                } else {
                    expectedSrcRootType = JavaSourceRootType.TEST_SOURCE;
                    expectedResourceRootType = JavaResourceRootType.TEST_RESOURCE;
                }

                if (sourceRoot.getRootType() == expectedSrcRootType) {
                    if (!(srcPath.length() == 0)) {
                        srcPath.append(File.pathSeparator);
                    }
                    srcPath.append(sourceRoot.getFile().getAbsolutePath());
                }
                if (sourceRoot.getRootType() == expectedResourceRootType) {
                    if (!(resourcePath.length() == 0)) {
                        resourcePath.append(File.pathSeparator);
                    }
                    resourcePath.append(sourceRoot.getFile().getAbsolutePath());
                }
            }
        }

        JpsCeylonModuleExtension facet = chunk.representativeTarget().getModule().getContainer()
                .getChild(JpsCeylonModuleExtension.KIND);
        options.add("-src");
        options.add(srcPath.toString());

        if (resourcePath.length() > 0) {
            options.add("-res");
            options.add(resourcePath.toString());
        }

        options.add("-g:lines,vars,source");

        File outputDir = chunk.representativeTarget().getOutputDir();
        if (outputDir != null) {
            options.add("-out");
            options.add(outputDir.getAbsolutePath());
        } else {
            throw new IllegalArgumentException("Can't detect compiler output path");
        }

        options.add("-sysrep");
        if (StringUtil.isNotEmpty(facet.getProperties().getSystemRepository())) {
            options.add(facet.getProperties().getSystemRepository());
        } else {
            options.add(getSystemRepoPath());
        }

        // TODO we should find where chunk.representativeTarget().getModule() is located instead
        JpsModuleSourceRoot root = chunk.representativeTarget().getModule().getSourceRoots().get(0);
        if (root != null) {
            options.add("-cwd");
            options.add(root.getFile().getParentFile().getAbsolutePath());

            try {
                CeylonConfig ceylonConfig = CeylonConfigFinder.loadLocalConfig(root.getFile());
                Repositories repositories = Repositories.withConfig(ceylonConfig);

                for (Repositories.Repository[] repo : repositories.getRepositories().values()) {
                    if (repo != null) {
                        for (Repositories.Repository r : repo) {
                            options.add("-rep");
                            options.add(r.getUrl());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return options;
    }

    private String getSystemRepoPath() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(CeylonCompiler.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(new File(pluginClassesDir, "embeddedDist"), "repo");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir.getAbsolutePath();
            }
        }

        throw new RuntimeException("Embedded Ceylon system repo not found");
    }

    private static final String[] searchedArchives = {
            "org.antlr.runtime",
            "org.jboss.modules",
            "com.github.rjeschke.txtmark",
            "com.redhat.ceylon.compiler.java",
            "com.redhat.ceylon.langtools.classfile",
            "com.redhat.ceylon.module-resolver",
            "com.redhat.ceylon.typechecker",
            "com.redhat.ceylon.model",
            "com.redhat.ceylon.common",
            "com.redhat.ceylon.cli",
            "ceylon.language"
    };

    private void searchArchivesFromRepo(File path, Map<String, File> filesToAddToClasspath) {
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                searchArchivesFromRepo(f, filesToAddToClasspath);
            }
        } else if (path.isFile()) {
            for (String searchedArchive : searchedArchives) {
                // TODO this is fragile (we have several modules that start with com.redhat.ceylon.module-resolver)
                if (path.getName().matches("^" + Pattern.quote(searchedArchive) + "-.+\\.(j|c)ar$")
                        && !filesToAddToClasspath.containsKey(searchedArchive)) {
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

        File ceylonRepoDir = new File(new File(pluginClassesDir, "embeddedDist"), "repo");
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
            List<String> options = buildOptions(chunk);
            Class<?> compilerClass = Class.forName("org.intellij.plugins.ceylon.compiler.CeylonCompiler", true, loader);
            Object compiler = compilerClass.newInstance();
            compilerClass
                    .getMethod("compile", CompileContext.class, ModuleChunk.class, List.class, List.class)
                    .invoke(compiler, ctx, chunk, filesToBuild, options);

            removeCompiledFilesFromContext(ctx, chunk, filesToBuild);
        } catch (ReflectiveOperationException e) {
            ctx.processMessage(new CompilerMessage(BUILDER_NAME, e));
            return ExitCode.ABORT;
        }

        return ExitCode.OK;
    }

    // Remove Java files from the list of files to process because we already compiled them
    private void removeCompiledFilesFromContext(CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) {
        FilesDelta delta = ctx.getProjectDescriptor().fsState
                .getEffectiveFilesDelta(ctx, chunk.representativeTarget());
        for (Map.Entry<BuildRootDescriptor, Set<File>> entry : delta.getSourcesToRecompile().entrySet()) {
            if (entry.getKey().getTarget() != chunk.representativeTarget()) {
                continue;
            }
            Iterator<File> it = entry.getValue().iterator();
            while (it.hasNext()) {
                if (filesToBuild.contains(it.next().getAbsolutePath())) {
                    it.remove();
                }
            }
        }
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Ceylon compiler";
    }
}
