package org.intellij.plugins.ceylon.jps;

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
import java.util.*;

import static com.redhat.ceylon.common.config.DefaultToolOptions.*;
import static com.redhat.ceylon.common.tools.ModuleWildcardsHelper.expandWildcards;

class CeylonBuilder extends ModuleLevelBuilder {

    private static final String BUILDER_NAME = "ceylon";
    private final Backend backend;

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

            return compile(context, chunk, filesToBuild);
        } catch (Exception e) {
            context.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR, "Could not launch compiler"));
            context.processMessage(new CompilerMessage(BUILDER_NAME, e));
            e.printStackTrace();
            return ExitCode.ABORT;
        }
    }

    private ExitCode compile(final CompileContext ctx, ModuleChunk chunk, List<String> filesToBuild) throws IOException {
        if (filesToBuild.isEmpty()) {
            return ExitCode.NOTHING_DONE;
        }

        Compiler compiler = CeylonToolProvider.getCompiler(backend);
        CompilerOptions options = buildOptions(chunk);

        if (options.getModules().isEmpty()) {
            ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                    "No Ceylon module to build for " + backend.name() + " backend."));
            return ExitCode.NOTHING_DONE;
        } else {
            ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                    "Building modules " + options.getModules()));
        }

        compiler.compile(options, new CompilationListener() {
            @Override
            public void error(File file, long line, long column, String message) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                        message, file.getAbsolutePath(), -1, -1, -1, line, column));
            }

            @Override
            public void warning(File file, long line, long column, String message) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.ERROR,
                        message, file.getAbsolutePath(), -1, -1, -1, line, column));
            }

            @Override
            public void moduleCompiled(String module, String version) {
                ctx.processMessage(new CompilerMessage(BUILDER_NAME, BuildMessage.Kind.INFO,
                        "Compiled module " + module + "/" + version));
            }
        });

        if (backend == Backend.Java) {
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
        FilesDelta delta = ctx.getProjectDescriptor().fsState
                .getEffectiveFilesDelta(ctx, chunk.representativeTarget());
        for (Map.Entry<BuildRootDescriptor, Set<File>> entry : delta.getSourcesToRecompile().entrySet()) {
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
        return "Ceylon compiler";
    }
}
