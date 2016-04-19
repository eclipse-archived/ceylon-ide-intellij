package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.Helper;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.typechecker.EditedPhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs.IdeaVirtualFolder;
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs.VirtualFileVirtualFile;
import org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TypeCheckerInvoker implements ITypeCheckerInvoker {
    /*
     * Mimics com.redhat.ceylon.eclipse.code.parse.CeylonParseController#parse
     */
    static PhasedUnit invokeTypeChecker(CeylonFile ceylonFile) {
        TypeChecker typeChecker = TypeCheckerProvider.getFor(ceylonFile);

        if (typeChecker == null) {
            return null;
        }
        if (ceylonFile.getPhasedUnit() instanceof ExternalPhasedUnit) {
            PhasedUnit pu = ceylonFile.getPhasedUnit();
            pu.analyseTypes();
            pu.analyseUsage();
            return pu;
        }
        return invokeTypeChecker(ceylonFile, typeChecker);
    }

    private static PhasedUnit invokeTypeChecker(CeylonFile ceylonFile, TypeChecker typeChecker) {
        com.intellij.openapi.module.Module module = ModuleUtil.findModuleForFile(
                ceylonFile.getVirtualFile(), ceylonFile.getProject());
        VirtualFileVirtualFile sourceCodeVirtualFile
                = new VirtualFileVirtualFile(ceylonFile.getVirtualFile(), module);
        PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);
        Tree.CompilationUnit cu = ceylonFile.getCompilationUnit();

        IdeaVirtualFolder srcDir;
        Package pkg;

        if (phasedUnit == null) {
            srcDir = getSourceFolder(ceylonFile, module);
            if (srcDir == null) { // happens eg. for *.ceylon files that are not within a source root. Don't do typechecking for these.
                return null;
            }
            pkg = getPackage(sourceCodeVirtualFile, srcDir, typeChecker);
        } else {
            srcDir = (IdeaVirtualFolder) phasedUnit.getSrcDir();
            pkg = phasedUnit.getPackage();
        }
        String relativePath = Helper.computeRelativePath(sourceCodeVirtualFile, srcDir);

        if (typeChecker.getPhasedUnitFromRelativePath(relativePath) != null) {
            typeChecker.getPhasedUnits().removePhasedUnitForRelativePath(relativePath);
        }

        ProjectPhasedUnit projectPu = null;
        if (phasedUnit instanceof EditedPhasedUnit) {
            projectPu = ((EditedPhasedUnit) phasedUnit).getOriginalPhasedUnit();
        } else if (phasedUnit instanceof ProjectPhasedUnit) {
            projectPu = (ProjectPhasedUnit) phasedUnit;
        }

        phasedUnit = new EditedPhasedUnit<>(
                TypeDescriptor.klass(com.intellij.openapi.module.Module.class),
                TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
                TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
                TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
                sourceCodeVirtualFile, srcDir, cu, pkg,
                typeChecker.getPhasedUnits().getModuleManager(),
                typeChecker.getPhasedUnits().getModuleSourceMapper(),
                typeChecker,
                ceylonFile.getTokens(),
                projectPu);

        BaseIdeModelLoader loader = ((BaseIdeModuleManager) typeChecker.getPhasedUnits()
                .getModuleManager()).getModelLoader();

        loader.loadPackageDescriptors();

        phasedUnit.validateTree();
        phasedUnit.visitSrcModulePhase();
        phasedUnit.visitRemainingModulePhase();
        phasedUnit.scanDeclarations();
        phasedUnit.scanTypeDeclarations();
        phasedUnit.validateRefinement();
        phasedUnit.analyseTypes();
        phasedUnit.analyseUsage();
        phasedUnit.analyseFlow();

        typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);

        ceylonFile.setPhasedUnit(phasedUnit);

        return phasedUnit;
    }

    private static IdeaVirtualFolder getSourceFolder(CeylonFile sourceFile, com.intellij.openapi.module.Module mod) {
        com.intellij.openapi.vfs.VirtualFile root = ProjectRootManager
                .getInstance(sourceFile.getProject())
                .getFileIndex()
                .getSourceRootForFile(sourceFile.getVirtualFile());

        return new IdeaVirtualFolder(root, mod);
    }

    private static Package getPackage(VirtualFile file, VirtualFile srcDir,
                                      TypeChecker typeChecker) {
        Modules modules = typeChecker.getContext().getModules();
        // Retrieve the target package from the file src-relative path
        //TODO: this is very fragile!
        String packageName = constructPackageName(file, srcDir);
        for (Module module : modules.getListOfModules()) {
            if (packageName.startsWith(module.getNameAsString())) {
                Package pkg = module.getDirectPackage(packageName);
                if (pkg == null) {
                    pkg = createPackage(packageName, module);
                }
                return pkg;
            }
        }
        // Contained in no existing module; assume the default package
        return modules.getDefaultModule().getPackages().get(0);
    }

    // createPackage is copied from ModuleManager.
    private static Package createPackage(String packageName, Module module) {
        Package pkg;
        pkg = new Package();
        List<String> name = packageName.isEmpty() ? Collections.singletonList("") : ModuleManager.splitModuleName(packageName);
        pkg.setName(name);
        if (module != null) {
            module.getPackages().add(pkg);
            pkg.setModule(module);
        }
        return pkg;
    }

    private static String constructPackageName(com.redhat.ceylon.compiler.typechecker.io.VirtualFile file, com.redhat.ceylon.compiler.typechecker.io.VirtualFile srcDir) {
        return file.getPath().substring(srcDir.getPath().length() + 1)
                .replace("/" + file.getName(), "").replace('/', '.');
    }

    @Override
    public PhasedUnit typecheck(CeylonFile ceylonFile) {
        return invokeTypeChecker(ceylonFile);
    }

    @Override
    public File getEmbeddedCeylonRepository() {
        return CeylonIdePlugin.getEmbeddedCeylonRepository();
    }

    @Override
    public File getEmbeddedCeylonDist() {
        return CeylonIdePlugin.getEmbeddedCeylonDist();
    }
}
