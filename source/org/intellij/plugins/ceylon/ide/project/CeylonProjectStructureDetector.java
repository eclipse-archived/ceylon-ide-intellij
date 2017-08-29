//package org.intellij.plugins.ceylon.ide.project;
//
//import com.intellij.ide.util.DelegatingProgressIndicator;
//import com.intellij.ide.util.importProject.LibrariesDetectionStep;
//import com.intellij.ide.util.importProject.ModuleInsight;
//import com.intellij.ide.util.importProject.ModulesDetectionStep;
//import com.intellij.ide.util.importProject.ProjectDescriptor;
//import com.intellij.ide.util.projectWizard.ModuleWizardStep;
//import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
//import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
//import com.intellij.ide.util.projectWizard.importSources.JavaModuleSourceRoot;
//import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
//import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector;
//import com.intellij.ide.util.projectWizard.importSources.util.CommonSourceRootDetectionUtil;
//import com.intellij.openapi.util.Pair;
//import com.intellij.util.NullableFunction;
//import com.intellij.util.Processor;
//import com.redhat.ceylon.common.config.CeylonConfig;
//import com.redhat.ceylon.common.config.CeylonConfigFinder;
//import com.redhat.ceylon.common.config.DefaultToolOptions;
//import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
//import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
//import com.redhat.ceylon.compiler.typechecker.tree.Tree;
//import org.antlr.runtime.ANTLRStringStream;
//import org.antlr.runtime.CommonTokenStream;
//import org.antlr.runtime.RecognitionException;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.intellij.openapi.util.io.FileUtil.processFilesRecursively;
//
//public class CeylonProjectStructureDetector extends ProjectStructureDetector {
//
//    @Override
//    public String getDetectorId() {
//        return "Ceylon";
//    }
//
//    @Override
//    public List<ModuleWizardStep> createWizardSteps(ProjectFromSourcesBuilder builder, ProjectDescriptor projectDescriptor, Icon stepIcon) {
//        List<ModuleWizardStep> steps = new ArrayList<>();
//        ModuleInsight moduleInsight = new CeylonModuleInsight(new DelegatingProgressIndicator(), builder.getExistingModuleNames(), builder.getExistingProjectLibraryNames());
//
//        steps.add(new LibrariesDetectionStep(builder, projectDescriptor, moduleInsight, stepIcon, "reference.dialogs.new.project.fromCode.page1"));
//        steps.add(new ModulesDetectionStep(this, builder, projectDescriptor, moduleInsight, stepIcon, "reference.dialogs.new.project.fromCode.page2"));
//        steps.add(ProjectWizardStepFactory.getInstance().createProjectJdkStep(builder.getContext()));
//
//        return steps;
//    }
//
//    @NotNull
//    @Override
//    public DirectoryProcessingResult detectRoots(@NotNull File dir, @NotNull File[] children, @NotNull File base, @NotNull final List<DetectedProjectRoot> result) {
//        // Try locating a Ceylon configuration in the root folder
//        if (dir == base) {
//            try {
//                File localConfig = CeylonConfigFinder.DEFAULT.findLocalConfig(base);
//                // try finding source roots in .ceylon/config
//                if (localConfig != null) {
//                    CeylonConfig config = CeylonConfigFinder.DEFAULT.loadConfigFromFile(localConfig);
//                    List<File> sourceDirs = DefaultToolOptions.getCompilerSourceDirs(config);
//                    for (File sourceDir : sourceDirs) {
//                        File absolute = sourceDir.isAbsolute() ? sourceDir : new File(base, sourceDir.getPath()).toPath().normalize().toFile();
//                        if (absolute.isDirectory()) {
//                            result.add(new JavaModuleSourceRoot(absolute, null, "Ceylon"));
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (result.isEmpty()) {
//                // try the default "source" folder
//                final File source = new File(base, "source");
//
//                if (source.isDirectory()) {
//                    processFilesRecursively(source, new Processor<File>() {
//                        @Override
//                        public boolean process(File file) {
//                            if (file.isFile() && file.getName().endsWith(".ceylon")) {
//                                result.add(new JavaModuleSourceRoot(source, null, "Ceylon"));
//                                return false;
//                            }
//                            return true;
//                        }
//                    });
//                }
//            }
//
//            if (!result.isEmpty()) {
//                return DirectoryProcessingResult.SKIP_CHILDREN;
//            }
//        }
//
//        // try scanning children for Ceylon files
//        for (File child : children) {
//            if (child.isFile() && child.getName().equals("module.ceylon")) {
//                Pair<File, String> root = CommonSourceRootDetectionUtil.IO_FILE
//                        .suggestRootForFileWithPackageStatement(child, base, getPackageNameFetcher(), true);
//                if (root != null) {
//                    result.add(new JavaModuleSourceRoot(root.getFirst(), root.getSecond(), "Ceylon"));
//                    return DirectoryProcessingResult.skipChildrenAndParentsUpTo(root.getFirst());
//                } else {
//                    return DirectoryProcessingResult.SKIP_CHILDREN;
//                }
//            }
//        }
//
//        return DirectoryProcessingResult.PROCESS_CHILDREN;
//    }
//
//    @NotNull
//    private NullableFunction<CharSequence, String> getPackageNameFetcher() {
//        return new NullableFunction<CharSequence, String>() {
//            @Nullable
//            @Override
//            public String fun(CharSequence charSequence) {
//                CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(charSequence.toString()));
//                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
//                CeylonParser parser = new CeylonParser(tokenStream);
//
//                try {
//                    Tree.CompilationUnit cu = parser.compilationUnit();
//
//                    if (!cu.getModuleDescriptors().isEmpty()) {
//                        StringBuilder builder = new StringBuilder();
//                        for (Tree.Identifier identifier : cu.getModuleDescriptors().get(0).getImportPath().getIdentifiers()) {
//                            builder.append(identifier.getToken().getText()).append('.');
//                        }
//
//                        if (builder.length() > 0) {
//                            builder.deleteCharAt(builder.length() - 1);
//                        }
//                        return builder.toString();
//                    }
//                } catch (RecognitionException e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//        };
//    }
//}
