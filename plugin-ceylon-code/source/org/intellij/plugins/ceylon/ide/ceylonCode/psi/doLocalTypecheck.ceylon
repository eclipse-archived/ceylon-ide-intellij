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
import com.redhat.ceylon.ide.common.platform {
    platformUtils
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
    Package
}

import java.lang {
    InterruptedException
}
import java.util.concurrent {
    TimeUnit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    findProjectForFile,
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    VirtualFileVirtualFile,
    IdeaVirtualFolder
}

"Operates a local typecheck of the [[file]], without updating the global model."
shared PhasedUnit? doLocalTypecheck(CeylonFile file) {
    if (exists project = findProjectForFile(file),
        project.typechecked,
        exists tc = project.typechecker) {
        
        return doWithSourceModel(project, true, 0, () {
            value mod = project.ideArtifact;
            value sourceCodeVirtualFile = VirtualFileVirtualFile(file.virtualFile, mod);
            PhasedUnit? phasedUnit = tc.getPhasedUnit(sourceCodeVirtualFile);
            value cu = file.compilationUnit;
            IdeaVirtualFolder srcDir;
            Package pkg;

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

            value projectPu = switch (phasedUnit)
            case (is EditedPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>) phasedUnit.originalPhasedUnit
            case (is ProjectPhasedUnit<Module,VirtualFile,VirtualFile,VirtualFile>) phasedUnit
            else null;

            value singleSourceUnitPackage = SingleSourceUnitPackage(pkg, sourceCodeVirtualFile.path);

            assert(is BaseIdeModuleSourceMapper msm = tc.phasedUnits.moduleSourceMapper);

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

            msm.moduleManager.modelLoader.loadPackageDescriptors();
            editedPhasedUnit.validateTree();
            editedPhasedUnit.visitSrcModulePhase();
            editedPhasedUnit.visitRemainingModulePhase();
            editedPhasedUnit.scanDeclarations();
            editedPhasedUnit.scanTypeDeclarations();
            editedPhasedUnit.validateRefinement();
            editedPhasedUnit.analyseTypes();
            editedPhasedUnit.analyseUsage();
            editedPhasedUnit.analyseFlow();

            file.phasedUnit = editedPhasedUnit;
            return editedPhasedUnit;
        });
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

"Allows synchronizing some code that touches the source-related
 Ceylon model, by setting up the typechecker, creating or 
 typechecking PhasedUnits, etc ...
 
 It's based on a ReentrantReadWriteLock.
 
 To avoid deadlock, it always takes a time limit,
 after which the it stops waiting for the source 
 model availability and throw an OperationCanceled Exception"
Result doWithSourceModel<Result>(IdeaCeylonProject project, Boolean readonly,
    Integer waitForModelInSeconds, Result() action) {
    
    value projectLock = project.sourceModelLock;
    value sourceModelLock = readonly then projectLock.readLock() else projectLock.writeLock();
    
    try {
        if (sourceModelLock.tryLock(waitForModelInSeconds, TimeUnit.seconds)) {
            try {
                return action();
            } finally {
                sourceModelLock.unlock();
            }
        } else {
            throw platformUtils.newOperationCanceledException(
                "The source model "
                + (readonly then "read" else "write")
                + " lock of project "
                + project.string + " could not be acquired within "
                + waitForModelInSeconds.string + " seconds"
            );
        }
    } catch (InterruptedException ie) {
        throw platformUtils.newOperationCanceledException(
            "The thread was interrupted "
            + "while waiting for the source model "
            + (readonly then "read" else "write")
            + " lock of project " + project.string
        );
    }
}