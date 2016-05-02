package org.intellij.plugins.ceylon.jps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.compiler.java.runtime.tools.*;
import com.redhat.ceylon.compiler.java.runtime.tools.Compiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.redhat.ceylon.common.config.DefaultToolOptions.*;
import static com.redhat.ceylon.common.tools.ModuleWildcardsHelper.expandWildcards;

class CeylonBuilder extends ModuleLevelBuilder {

    private static final String BUILDER_NAME = "ceylon";
    private final Backend backend;
    private static final Logger logger = Logger.getInstance(CeylonBuilder.class);

    CeylonBuilder(Backend backend, BuilderCategory category) {
        super(category);
        this.backend = backend;
    }

    @Override
    public List<String> getCompilableFileExtensions() {
        return backend == Backend.Java
                ? Arrays.asList("java", "ceylon")
                : Arrays.asList("js", "ceylon");
    }

    @Override
    public ExitCode build(final CompileContext context, ModuleChunk chunk,
                          DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder,
                          OutputConsumer outputConsumer) throws ProjectBuildException, IOException {

        JpsCeylonGlobalSettings settings = JpsCeylonGlobalSettings.INSTANCE;
        if (settings == null) {
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                    "Could not read settings from JpsCeylonGlobalSettings.INSTANCE"));
            return ExitCode.ABORT;
        } else if (!settings.isUseOutProcessBuild()) {
            return ExitCode.NOTHING_DONE;
        }

        final Map<JpsModule, Boolean> compileForBackend = new HashMap<>();
        for (JpsModule module : chunk.getModules()) {
            JpsCeylonModuleExtension facet = module.getContainer().getChild(JpsCeylonModuleExtension.KIND);

            if (facet != null) {
                boolean sameBackend = backend == Backend.Java
                        ? facet.getProperties().isCompileForJvm()
                        : facet.getProperties().isCompileToJs();
                compileForBackend.put(module, sameBackend);
            } else {
                compileForBackend.put(module, false);
            }
        }

        if (compileForBackend.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        try {
            final List<String> filesToBuild = new ArrayList<>();

            dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                @Override
                public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
                    if (Boolean.TRUE.equals(compileForBackend.get(target.getModule()))
                            && getCompilableFileExtensions().contains(FileUtilRt.getExtension(file.getName()))) {
                        String path = file.getAbsolutePath();
                        filesToBuild.add(path);
                    }
                    return true;
                }
            });

            return compile(context, chunk, filesToBuild, settings);
        } catch (Exception e) {
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, "Could not launch compiler"));
            context.processMessage(new CompilerMessage(BUILDER_NAME, e));
            e.printStackTrace();
            return ExitCode.ABORT;
        }
    }

    private ExitCode compile(final CompileContext ctx,
                             ModuleChunk chunk,
                             List<String> filesToBuild,
                             JpsCeylonGlobalSettings settings) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        Compiler compiler = CeylonToolProvider.getCompiler(backend);
        CompilerOptions options = buildOptions(chunk);

        String message;
        if (options.getModules().isEmpty()) {
            message = "No Ceylon module to build for " + backend.name() + " backend.";
        } else {
            message = "Building modules " + options.getModules() + " using the " + backend.name() + " backend.";
        }

        ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                message));
        logger.info(message);

        if (options.getModules().isEmpty()) {
            logger.info("Nothing done");
            return ExitCode.NOTHING_DONE;
        }

        CompileContextWriter writer = new CompileContextWriter(ctx);
        options.setOutWriter(writer);
        if (settings.isMakeCompilerVerbose()) {
            options.setVerbose(true);
            if (!StringUtil.isEmptyOrSpaces(settings.getVerbosityLevel())) {
                options.setVerboseCategory(settings.getVerbosityLevel());
            }
        }

        boolean compileResult = compiler.compile(options, new CompilationListener() {
            @Override
            public void error(File file, long line, long column, String message) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                        message, file.getAbsolutePath(), -1, -1, -1, line, column));
            }

            @Override
            public void warning(File file, long line, long column, String message) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.WARNING,
                        message, file.getAbsolutePath(), -1, -1, -1, line, column));
            }

            @Override
            public void moduleCompiled(String module, String version) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                        "Compiled module " + module + "/" + version));
            }
        });

        writer.close();

        if (!compileResult) {
            logger.error("Compiler returned false!", (Throwable) null);
            ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                    "An unknown error occurred in the " + backend.name() + " backend. " +
                            "See previous messages for more information."));
        }

        if (backend == Backend.Java) {
            logger.info("Removing Java files that were already compiled from the context");
            removeCompiledJavaFilesFromContext(ctx, chunk, filesToBuild);
        }

        return ExitCode.OK;
    }

    private CompilerOptions buildOptions(ModuleChunk chunk) {
        CompilerOptions options = (backend == Backend.Java)
                ? new JavaCompilerOptions()
                : new CompilerOptions();

        List<File> srcPath = new ArrayList<>();
        List<File> resourcePath = new ArrayList<>();

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                JpsModuleSourceRootType<?> expectedSrcRootType;
                JpsModuleSourceRootType<?> expectedResourceRootType;

                if (chunk.containsTests()) {
                    expectedSrcRootType = JavaSourceRootType.TEST_SOURCE;
                    expectedResourceRootType = JavaResourceRootType.TEST_RESOURCE;
                } else {
                    expectedSrcRootType = JavaSourceRootType.SOURCE;
                    expectedResourceRootType = JavaResourceRootType.RESOURCE;
                }

                if (sourceRoot.getRootType() == expectedSrcRootType) {
                    srcPath.add(sourceRoot.getFile());
                }
                if (sourceRoot.getRootType() == expectedResourceRootType) {
                    resourcePath.add(sourceRoot.getFile());
                }
            }
        }

        JpsCeylonModuleExtension facet = chunk.representativeTarget().getModule().getContainer()
                .getChild(JpsCeylonModuleExtension.KIND);

        options.setSourcePath(srcPath);
        options.setResourcePath(resourcePath);

        File outputDir = chunk.representativeTarget().getOutputDir();
        if (outputDir != null) {
            options.setOutputRepository(outputDir.getAbsolutePath());
        } else {
            throw new IllegalArgumentException("Can't detect compiler output path");
        }

        if (StringUtil.isNotEmpty(facet.getProperties().getSystemRepository())) {
            options.setSystemRepository(facet.getProperties().getSystemRepository());
        } else {
            options.setSystemRepository(getSystemRepoPath());
        }

        // TODO we should find where chunk.representativeTarget().getModule() is located instead
        JpsModuleSourceRoot root = chunk.representativeTarget().getModule().getSourceRoots().get(0);
        if (root != null) {
            options.setWorkingDirectory(root.getFile().getParentFile().getAbsolutePath());

            try {
                CeylonConfig ceylonConfig = CeylonConfigFinder.loadLocalConfig(root.getFile());
                Repositories repositories = Repositories.withConfig(ceylonConfig);

                for (Repositories.Repository[] repo : repositories.getRepositories().values()) {
                    if (repo != null) {
                        for (Repositories.Repository r : repo) {
                            options.addUserRepository(r.getUrl());
                        }
                    }
                }

                options.setOffline(getDefaultOffline(ceylonConfig));

                if (options instanceof JavaCompilerOptions) {
                    JavaCompilerOptions javaOptions = (JavaCompilerOptions) options;
                    javaOptions.setFlatClasspath(getDefaultFlatClasspath(ceylonConfig));
                    javaOptions.setAutoExportMavenDependencies(getDefaultAutoExportMavenDependencies(ceylonConfig));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> moduleNames = expandWildcards(
                getSourceRoots(chunk),
                Collections.singletonList("*"),
                (backend == Backend.Java)
                        ? com.redhat.ceylon.common.Backend.Java
                        : com.redhat.ceylon.common.Backend.JavaScript
        );
        options.setModules(moduleNames);

        return options;
    }

    private List<File> getSourceRoots(ModuleChunk chunk) {
        List<File> roots = new ArrayList<>();

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                roots.add(sourceRoot.getFile());
            }
        }

        return roots;
    }

    private String getSystemRepoPath() {
        File pluginClassesDir = new File(PathUtil.getJarPathForClass(CeylonBuilder.class));

        if (pluginClassesDir.isDirectory()) {
            File ceylonRepoDir = new File(new File(pluginClassesDir, "embeddedDist"), "repo");
            if (ceylonRepoDir.exists()) {
                return ceylonRepoDir.getAbsolutePath();
            }
        }

        throw new RuntimeException("Embedded Ceylon system repo not found");
    }

    // Remove Java files from the list of files to process because we already compiled them
    private void removeCompiledJavaFilesFromContext(CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) {
        Map<BuildRootDescriptor, Set<File>> delta = ctx.getProjectDescriptor().fsState
                .getSourcesToRecompile(ctx, chunk.representativeTarget());

        for (Map.Entry<BuildRootDescriptor, Set<File>> entry : delta.entrySet()) {
            if (entry.getKey().getTarget() != chunk.representativeTarget()) {
                continue;
            }
            Iterator<File> it = entry.getValue().iterator();
            while (it.hasNext()) {
                File file = it.next();
                if (filesToBuild.contains(file.getAbsolutePath())
                        && file.getName().endsWith(".java")) {
                    it.remove();
                }
            }
        }
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Ceylon " + backend.name() + " compiler";
    }

    private static class CompileContextWriter extends Writer {
        private final CompileContext ctx;
        private String previousLine;

        CompileContextWriter(CompileContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            String line = new String(cbuf, off, len);
            if (StringUtil.isEmptyOrSpaces(line)) {
                return;
            }
            if (line.endsWith("\n")) {
                line = line.substring(0, line.length() - 1); // remove \n
            }
            logger.info(line);
            BuildMessage.Kind kind = BuildMessage.Kind.INFO;
            BuildMessage.Kind previousKind = BuildMessage.Kind.INFO;

            if (line.startsWith("An exception has occurred in the compiler")
                    || line.startsWith("\n\nAn input/output error occurred.")
                    || line.startsWith("\n\nAn annotation processor threw an uncaught exception.")) {

                kind = BuildMessage.Kind.ERROR;
            }
            if (line.startsWith("\tat ") || line.startsWith("\t... ")) {
                kind = BuildMessage.Kind.ERROR;
                previousKind = BuildMessage.Kind.ERROR;
            }

            if (previousLine != null) {
                log(previousLine, previousKind);
                previousLine = null;
            }

            if (kind == BuildMessage.Kind.INFO) {
                previousLine = line;
            } else {
                log(line, kind);
            }
        }

        private void log(String msg, BuildMessage.Kind kind) {
            ctx.processMessage(new CompilerMessage(
                    BUILDER_NAME,
                    kind,
                    msg
            ));
        }

        @Override
        public void flush() throws IOException {}

        @Override
        public void close() throws IOException {
            if (previousLine != null) {
                log(previousLine, BuildMessage.Kind.INFO);
            }
        }
    }
}
