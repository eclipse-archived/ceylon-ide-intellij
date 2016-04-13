import com.intellij.openapi.\imodule {
    Module,
    ModuleManager
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtilCore,
    VirtualFileVisitor,
    VfsUtil
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices
}
import com.redhat.ceylon.ide.common.model {
    EditedSourceFile,
    ProjectSourceFile,
    CrossProjectSourceFile,
    CeylonProject
}
import com.redhat.ceylon.ide.common.typechecker {
    EditedPhasedUnit,
    ProjectPhasedUnit,
    CrossProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.model.parsing {
    RootFolderScanner
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import ceylon.interop.java {
    CeylonIterable
}
import org.jetbrains.jps.model.java {
    JavaResourceRootType,
    JavaSourceRootType
}

shared object ideaModelServices satisfies ModelServices<Module, VirtualFile, VirtualFile,VirtualFile> {
    newCrossProjectSourceFile(CrossProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => CrossProjectSourceFile(phasedUnit);
    
    newEditedSourceFile(EditedPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => EditedSourceFile(phasedUnit);
    
    newProjectSourceFile(ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile> phasedUnit)
            => ProjectSourceFile(phasedUnit);

    shared actual Boolean isResourceContainedInProject(VirtualFile resource, IdeProject ceylonProject) {
        for (root in ModuleRootManager.getInstance(ceylonProject.ideArtifact).sourceRoots) {
            if (VfsUtil.isAncestor(root, resource, true)) {
                return true;
            }
        }
        return false;
    }


    // TODO check if the module is open?
    nativeProjectIsAccessible(Module nativeProject) => true;

    referencedNativeProjects(Module mod)
            => ModuleRootManager.getInstance(mod).dependencies.array.coalesced;

    referencingNativeProjects(Module mod)
            => CeylonIterable(ModuleManager.getInstance(mod.project)
                    .getModuleDependentModules(mod));

    shared alias IdeProject => CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>;

    shared actual {VirtualFile*} resourceNativeFolders(IdeProject ceylonProject) {
        value roots = ModuleRootManager.getInstance(ceylonProject.ideArtifact)
            ?.getSourceRoots(JavaResourceRootType.\iRESOURCE);

        return if (exists roots) then CeylonIterable(roots) else empty;
    }

    scanRootFolder(RootFolderScanner<Module,VirtualFile,VirtualFile,VirtualFile> scanner)
        => VfsUtilCore.visitChildrenRecursively(scanner.nativeRootDir,
            object extends VirtualFileVisitor<Nothing>() {
                visitFile(VirtualFile file) => scanner.visitNativeResource(file);
            }
        );

    shared actual {VirtualFile*} sourceNativeFolders(IdeProject ceylonProject) {
        value roots = ModuleRootManager.getInstance(ceylonProject.ideArtifact)
            ?.getSourceRoots(JavaSourceRootType.\iSOURCE);

        return if (exists roots) then CeylonIterable(roots) else empty;
    }

}