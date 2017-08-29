import com.intellij.ide.util.importProject {
    ModuleDescriptor,
    ModuleInsight
}
import com.intellij.ide.util.projectWizard.importSources {
    DetectedProjectRoot,
    DetectedSourceRoot,
    JavaModuleSourceRoot
}
import com.intellij.openapi.progress {
    ProgressIndicator
}
import com.intellij.util {
    Consumer
}

import java.io {
    File
}
import java.lang {
    CharSequence,
    Str=String
}
import java.util {
    Collection,
    Set
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonFileType
}

shared class CeylonModuleInsight(ProgressIndicator progress, Set<Str> existingModuleNames, Set<Str> existingProjectLibraryNames)
        extends ModuleInsight(progress, existingModuleNames, existingProjectLibraryNames) {

    createModuleDescriptor(File moduleContentRoot, Collection<DetectedSourceRoot> sourceRoots)
            => ModuleDescriptor(moduleContentRoot, CeylonModuleType.instance, sourceRoots);

    isApplicableRoot(DetectedProjectRoot root)
            => root is JavaModuleSourceRoot;

    isSourceFile(File file)
            => file.file
            && file.name.endsWith(CeylonFileType.instance.defaultExtension);

    isLibraryFile(String fileName) => false;

    shared actual void scanSourceFileForImportedPackages(CharSequence chars, Consumer<Str> result) {
    }

    shared actual void scanLibraryForDeclaredPackages(File file, Consumer<Str> result) {
    }

}
