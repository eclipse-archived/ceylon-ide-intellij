import ceylon.language {
    nil=null
}

import com.intellij.ide.util {
    DelegatingProgressIndicator
}
import com.intellij.ide.util.importProject {
    LibrariesDetectionStep,
    ModulesDetectionStep,
    ProjectDescriptor
}
import com.intellij.ide.util.projectWizard {
    ModuleWizardStep,
    ProjectWizardStepFactory
}
import com.intellij.ide.util.projectWizard.importSources {
    DetectedProjectRoot,
    JavaModuleSourceRoot,
    ProjectFromSourcesBuilder,
    ProjectStructureDetector {
        DirectoryProcessingResult {
            processChildren=\iPROCESS_CHILDREN,
            skipChildren=\iSKIP_CHILDREN,
            skipChildrenAndParentsUpTo
        }
    }
}
import com.intellij.ide.util.projectWizard.importSources.util {
    CommonSourceRootDetectionUtil
}
import com.intellij.openapi.util.io {
    FileUtil {
        processFilesRecursively
    }
}
import com.redhat.ceylon.common.config {
    CeylonConfigFinder,
    DefaultToolOptions
}
import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonLexer,
    CeylonParser
}

import java.io {
    File,
    IOException
}
import java.lang {
    ObjectArray,
    CharSequence
}
import java.util {
    ArrayList,
    List
}

import org.antlr.runtime {
    ANTLRStringStream,
    CommonTokenStream,
    RecognitionException
}
import javax.swing {
    Icon
}

shared class CeylonProjectStructureDetector() extends ProjectStructureDetector() {

    detectorId => "Ceylon";

    shared actual List<ModuleWizardStep> createWizardSteps(ProjectFromSourcesBuilder builder, ProjectDescriptor projectDescriptor, Icon stepIcon) {
        value steps = ArrayList<ModuleWizardStep>();
        value moduleInsight = CeylonModuleInsight(DelegatingProgressIndicator(), builder.existingModuleNames, builder.existingProjectLibraryNames);
        steps.add(LibrariesDetectionStep(builder, projectDescriptor, moduleInsight, stepIcon, "reference.dialogs.new.project.fromCode.page1"));
        steps.add(ModulesDetectionStep(this, builder, projectDescriptor, moduleInsight, stepIcon, "reference.dialogs.new.project.fromCode.page2"));
        steps.add(ProjectWizardStepFactory.instance.createProjectJdkStep(builder.context));
        return steps;
    }

    shared actual DirectoryProcessingResult detectRoots(File dir, ObjectArray<File> children, File base, List<DetectedProjectRoot> result) {
        if (dir == base) {
            try {
                value localConfig = CeylonConfigFinder.default.findLocalConfig(base);
                if (exists localConfig) {
                    value config = CeylonConfigFinder.default.loadConfigFromFile(localConfig);
                    value sourceDirs = DefaultToolOptions.getCompilerSourceDirs(config);
                    for (sourceDir in sourceDirs) {
                        value absolute =
                                if (sourceDir.absolute)
                                then sourceDir
                                else File(base, sourceDir.path)
                                        .toPath()
                                        .normalize()
                                        .toFile();
                        if (absolute.directory) {
                            result.add(JavaModuleSourceRoot(absolute, null, "Ceylon"));
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (result.empty) {
                value source = File(base, "source");
                if (source.directory) {
                    processFilesRecursively(source, (File file) {
                        if (file.file, file.name.endsWith(".ceylon")) {
                            result.add(JavaModuleSourceRoot(source, null, "Ceylon"));
                            return false;
                        }
                        return true;
                    });
                }
            }
            if (!result.empty) {
                return skipChildren;
            }
        }
        for (child in children) {
            if (child.file && child.name=="module.ceylon") {
                if (exists root
                        = CommonSourceRootDetectionUtil.ioFile
                        .suggestRootForFileWithPackageStatement(child, base, packageNameFetcher, true)) {
                    result.add(JavaModuleSourceRoot(root.first, root.second.string, "Ceylon"));
                    return skipChildrenAndParentsUpTo(root.first);
                } else {
                    return skipChildren;
                }
            }
        }
        return processChildren;
    }

    String? packageNameFetcher(CharSequence? charSequence) {
        if (!exists charSequence) {
            return null;
        }
        value lexer = CeylonLexer(ANTLRStringStream(charSequence.string));
        value tokenStream = CommonTokenStream(lexer);
        value parser = CeylonParser(tokenStream);
        try {
            value cu = parser.compilationUnit();
            if (exists path = cu.moduleDescriptors[0]?.importPath) {
                value builder = StringBuilder();
                for (identifier in path.identifiers) {
                    if (!builder.empty) {
                        builder.appendCharacter('.');
                    }
                    builder.append(identifier.token.text);
                }
                return builder.string;
            }
        }
        catch (RecognitionException e) {
            e.printStackTrace();
        }
        return nil;
    }

}
