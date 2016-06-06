import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.roots {
    ProjectRootManager
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.model {
    BaseIdeModuleSourceMapper
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit,
    EditedPhasedUnit,
    ProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    SingleSourceUnitPackage
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Cancellable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    findProjectForFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    VirtualFileVirtualFile,
    IdeaVirtualFolder
}
import com.intellij.openapi.progress {
    ProcessCanceledException
}

"Operates a local typecheck of the [[file]], without updating the global model."
shared PhasedUnit? doLocalTypecheck(CeylonFile file, Cancellable? cancellable = null) {
    void checkCancelled() {
        if (exists cancellable,
            cancellable.cancelled) {
            throw ProcessCanceledException();
        }
    }

    if (exists project = findProjectForFile(file),
        project.typechecked,
        exists tc = project.typechecker) {

        checkCancelled();

        return project.withSourceModel(true, () {
            value mod = project.ideArtifact;
            value sourceCodeVirtualFile = VirtualFileVirtualFile(file.virtualFile, mod);
            PhasedUnit? phasedUnit = tc.getPhasedUnit(sourceCodeVirtualFile);
            value cu = file.compilationUnit;
            IdeaVirtualFolder srcDir;
            Package pkg;

            checkCancelled();

            if (exists phasedUnit) {
                assert(is IdeaVirtualFolder f = phasedUnit.srcDir);
                srcDir = f;
                pkg = phasedUnit.\ipackage;
            } else {
                value sf = getSourceFolder(file, mod);

                if (!exists sf) {
                    return null;
                }
                srcDir = sf;
                assert(exists cp = srcDir.ceylonPackage);
                pkg = cp;
            }
            
            checkCancelled();
            
            value projectPu = switch (phasedUnit)
            case (is EditedPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>) phasedUnit.originalPhasedUnit
            case (is ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>) phasedUnit
            else null;

            value singleSourceUnitPackage = SingleSourceUnitPackage(pkg, sourceCodeVirtualFile.path);

            assert(is BaseIdeModuleSourceMapper msm = tc.phasedUnits.moduleSourceMapper);
            
            checkCancelled();
            
            value editedPhasedUnit = EditedPhasedUnit {
                unitFile = sourceCodeVirtualFile;
                srcDir = srcDir;
                cu = cu;
                p = singleSourceUnitPackage;
                moduleManager = tc.phasedUnits.moduleManager;
                moduleSourceMapper = msm;
                typeChecker = tc;
                tokens = file.tokens;
                savedPhasedUnit = projectPu;
                project = mod;
                file = file.virtualFile;
            };
            
            checkCancelled();
            
            msm.moduleManager.modelLoader.loadPackageDescriptors();
            
            value phases = [
                editedPhasedUnit.validateTree,
                () { editedPhasedUnit.visitSrcModulePhase(); },
                editedPhasedUnit.visitRemainingModulePhase,
                editedPhasedUnit.scanDeclarations,
                editedPhasedUnit.scanTypeDeclarations,
                editedPhasedUnit.validateRefinement,
                editedPhasedUnit.analyseTypes,
                editedPhasedUnit.analyseUsage,
                editedPhasedUnit.analyseFlow
            ];
                
                checkCancelled();
            
            for (phase in phases) {
                phase();
                checkCancelled();
            }

            file.phasedUnit = editedPhasedUnit;
            return editedPhasedUnit;
        }, 0);
    } else if (is ExternalPhasedUnit unit = file.phasedUnit) {
        unit.analyseTypes();
        unit.analyseUsage();
        return unit;
    }
    
    return null;
}

IdeaVirtualFolder? getSourceFolder(CeylonFile sourceFile, Module mod) {
    VirtualFile? root = ProjectRootManager.getInstance(sourceFile.project)
        .fileIndex.getSourceRootForFile(sourceFile.virtualFile);
    return if (exists root) then IdeaVirtualFolder(root, mod) else null;
}
