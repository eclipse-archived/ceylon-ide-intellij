package org.intellij.plugins.ceylon.jps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.compiler.java.runtime.tools.*;
import com.redhat.ceylon.compiler.java.runtime.tools.Compiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.ProjectPaths;
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.redhat.ceylon.common.tools.ModuleWildcardsHelper.expandWildcards;
import static java.nio.file.Files.readAllLines;
import static org.jetbrains.jps.builders.java.JavaBuilderUtil.isForcedRecompilationAllJavaModules;

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

        final Map<JpsModule, Boolean> compileForBackend = new HashMap<JpsModule, Boolean>();
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

        if (!compileForBackend.values().contains(true)) {
            return ExitCode.NOTHING_DONE;
        }

        try {
            final Set<File> filesToBuild = new HashSet<File>();
            File storage = context.getProjectDescriptor().dataManager.getDataPaths().getDataStorageRoot();
            File ceylonFiles = new File(storage, "ceylonFiles-" + backend.name() + ".txt");
            if (ceylonFiles.isFile()) {
                for (String line : readAllLines(ceylonFiles.toPath(), StandardCharsets.UTF_8)) {
                    filesToBuild.add(new File(line));
                }
            }
            /*dirtyFilesHolder.processDirtyFiles(new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
                @Override
                public boolean apply(ModuleBuildTarget target, File file, JavaSourceRootDescriptor root) throws IOException {
                    if (Boolean.TRUE.equals(compileForBackend.get(target.getModule()))
                            && getCompilableFileExtensions().contains(FileUtilRt.getExtension(file.getName()))) {
                        filesToBuild.add(file);
                    }
                    return true;
                }
            });*/

            return compile(context, chunk, new ArrayList<File>(filesToBuild), settings);
        } catch (Exception e) {
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, "Could not launch compiler"));
            context.processMessage(new CompilerMessage(BUILDER_NAME, e));
            e.printStackTrace();
            return ExitCode.ABORT;
        }
    }

    private ExitCode compile(final CompileContext ctx,
                             ModuleChunk chunk,
                             List<File> filesToBuild,
                             JpsCeylonGlobalSettings settings) throws IOException {

        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        boolean fullBuild = isForcedRecompilationAllJavaModules(ctx);
        Compiler compiler = CeylonToolProvider.getCompiler(backend);
        CompilerOptions options = buildOptions(chunk, filesToBuild, fullBuild);

        String message;
        if (backend == Backend.Java && !fullBuild) {
            message = "Incremental build for " + backend.name() + " backend.";
        } else if (options.getModules().isEmpty()) {
            message = "No Ceylon module to build for " + backend.name() + " backend.";
        } else {
            message = "Building modules " + options.getModules() + " using the " + backend.name() + " backend.";
        }

        ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                message));
        logger.info(message);

        if (options.getModules().isEmpty() && options.getFiles().isEmpty()) {
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
                String absolutePath = file == null ? null : file.getAbsolutePath();
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                        message, absolutePath, -1, -1, -1, line, column));
            }

            @Override
            public void warning(File file, long line, long column, String message) {
                String absolutePath = file == null ? null : file.getAbsolutePath();
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.WARNING,
                        message, absolutePath, -1, -1, -1, line, column));
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

    private CompilerOptions buildOptions(ModuleChunk chunk, List<File> filesToBuild, boolean fullBuild) {
        // TODO we should find where chunk.representativeTarget().getModule() is located instead
        JpsModuleSourceRoot root = chunk.representativeTarget().getModule().getSourceRoots().get(0);
        CeylonConfig config;
        if (root == null) {
            config = new CeylonConfig();
        } else {
            try {
                config = CeylonConfigFinder.loadLocalConfig(root.getFile());
            } catch (IOException e) {
                config = new CeylonConfig();
            }
        }

        CompilerOptions options = (backend == Backend.Java)
                ? JavaCompilerOptions.fromConfig(config)
                : CompilerOptions.fromConfig(config);

        // Undo unwanted behavior (defaults to unexpanded "*")
        options.setModules(Collections.<String>emptyList());

        JpsCeylonModuleExtension facet = chunk.representativeTarget().getModule().getContainer()
                .getChild(JpsCeylonModuleExtension.KIND);

        if (StringUtil.isNotEmpty(facet.getProperties().getSystemRepository())) {
            options.setSystemRepository(facet.getProperties().getSystemRepository());
        } else {
            options.setSystemRepository(getSystemRepoPath());
        }

        if (root != null) {
            options.setWorkingDirectory(root.getFile().getParentFile().getAbsolutePath());
            List<File> absoluteSources = new ArrayList<File>();
            for (File path : options.getSourcePath()) {
                absoluteSources.add(new File(root.getFile().getParentFile(), path.getPath()));
            }
            options.setSourcePath(absoluteSources);

            List<File> absoluteResources = new ArrayList<File>();
            for (File path : options.getResourcePath()) {
                absoluteResources.add(new File(root.getFile().getParentFile(), path.getPath()));
            }
            options.setResourcePath(absoluteResources);
        }

        // Only the Java backend can do incremental builds
        if (backend == Backend.Java && !fullBuild) {
            List<File> filteredFiles = new ArrayList<File>();

            // filesToBuild contains file for every module we're building, we need to filter
            // files for the current module only.
            List<File> modulePaths = ContainerUtil.concat(options.getSourcePath(), options.getResourcePath());
            for (File f : filesToBuild) {
                for (File src : modulePaths) {
                    if (f.toPath().startsWith(src.toPath())) {
                        filteredFiles.add(f);
                        break;
                    }
                }
            }

            options.setFiles(filteredFiles);
        } else {
            List<String> moduleNames = new ArrayList<String>(expandWildcards(
                    getSourceRoots(chunk),
                    Collections.singletonList("*"),
                    (backend == Backend.Java)
                            ? com.redhat.ceylon.common.Backend.Java
                            : com.redhat.ceylon.common.Backend.JavaScript
            ));
            if (defaultModulePresent(chunk)) {
                moduleNames.add("default");
            }
            options.setModules(moduleNames);
        }

        return options;
    }

    private boolean defaultModulePresent(ModuleChunk chunk) {
        for (File root : getSourceRoots(chunk)) {
            if (containsDefaultModule(root)) {
                return true;
            }
        }
        return false;
    }

    private FilenameFilter ceylonFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".ceylon")
                    && !new File(dir, "module.ceylon").exists();
        }
    };
    private boolean containsDefaultModule(File file) {
        File[] files = file.listFiles(ceylonFilter);
        if (files != null && files.length > 0) {
            return true;
        }

        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory() && containsDefaultModule(child)) {
                    return true;
                }
                if (child.isFile() && child.getName().equals("module.ceylon")) {
                    return false;
                }
            }
        }
        return false;
    }

    private List<File> getSourceRoots(ModuleChunk chunk) {
        List<File> roots = new ArrayList<File>();
        JpsModuleSourceRootType<?> expectedSrcRootType;

        if (chunk.containsTests()) {
            expectedSrcRootType = JavaSourceRootType.TEST_SOURCE;
        } else {
            expectedSrcRootType = JavaSourceRootType.SOURCE;
        }

        for (JpsModule module : chunk.getModules()) {
            for (JpsModuleSourceRoot sourceRoot : module.getSourceRoots()) {
                if (sourceRoot.getRootType() == expectedSrcRootType) {
                    roots.add(sourceRoot.getFile());
                }
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
    private void removeCompiledJavaFilesFromContext(CompileContext ctx, ModuleChunk chunk, List<File> filesToBuild) {
        FilesDelta delta = ctx.getProjectDescriptor().fsState
                .getEffectiveFilesDelta(ctx, chunk.representativeTarget());
        try {
            delta.lockData();

            for (Map.Entry<BuildRootDescriptor, Set<File>> entry : delta.getSourcesToRecompile().entrySet()) {
                if (entry.getKey().getTarget() != chunk.representativeTarget()) {
                    continue;
                }
                Iterator<File> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    if (filesToBuild.contains(file)
                            && file.getName().endsWith(".java")) {
                        it.remove();
                    }
                }
            }
        } finally {
            delta.unlockData();
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
