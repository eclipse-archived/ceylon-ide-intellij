package org.intellij.plugins.ceylon.ide.annotator;

import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin;

import java.io.File;

public class TypeCheckerInvoker implements ITypeCheckerInvoker {
//    /*
//     * Mimics com.redhat.ceylon.eclipse.code.parse.CeylonParseController#parse
//     */
//    static PhasedUnit invokeTypeChecker(CeylonFile ceylonFile) {
//        TypeChecker typeChecker = TypeCheckerProvider.getFor(ceylonFile);
//
//        if (typeChecker == null) {
//            return null;
//        }
//        if (ceylonFile.getPhasedUnit() instanceof ExternalPhasedUnit) {
//            PhasedUnit pu = ceylonFile.getPhasedUnit();
//            pu.analyseTypes();
//            pu.analyseUsage();
//            return pu;
//        }
//        return invokeTypeChecker(ceylonFile, typeChecker);
//    }
//
//    private static ReadWriteLock getProjectSourceModelLock(BaseCeylonProject ceylonProject) {
//        return ceylonProject != null ? ceylonProject.getSourceModelLock() : null;
//    }
//
//    /*
//     * Allows synchronizing some code that touches the source-related
//     * Ceylon model, by setting up the typechecker, creating or
//     * typechecking PhasedUnits, etc ...
//     *
//     * It's based on a ReentrantReadWriteLock.
//     *
//     * To avoid deadlock, it always takes a time limit,
//     * after which the it stops waiting for the source
//     * model availability and throw an OperationCanceled Exception
//     *
//     */
//    public static <T> T doWithSourceModel(
//                                BaseCeylonProject project,
//                                boolean readonly,
//                                final long waitForModelInSeconds,
//                                Callable<T> action) {
//        try {
//            if (project== null) {
//                return action.call();
//            }
//            ReadWriteLock projectLock = getProjectSourceModelLock(project);
//            if (projectLock == null) {
//                return action.call();
//            }
//            Lock sourceModelLock =
//                    readonly ?
//                        projectLock.readLock() :
//                            projectLock.writeLock();
//            if (sourceModelLock.tryLock(waitForModelInSeconds, TimeUnit.SECONDS)) {
//                try {
//                    return action.call();
//                } finally {
//                    sourceModelLock.unlock();
//                }
//            } else {
//                throw platformUtils_.get_().newOperationCanceledException("The source model "
//                        + (readonly ? "read" : "write")
//                        + " lock of project "
//                        + project + " could not be acquired within "
//                            + waitForModelInSeconds+ " seconds");
//            }
//        } catch(InterruptedException ie) {
//            throw platformUtils_.get_().newOperationCanceledException("The thread was interrupted "
//                    + "while waiting for the source model "
//                    + (readonly ? "read" : "write")
//                    + " lock of project " + project);
//        } catch(Exception e) {
//            if (e instanceof RuntimeException) {
//                throw (RuntimeException) e;
//            } else {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    @SuppressWarnings("rawtypes")
//    private static PhasedUnit invokeTypeChecker(final CeylonFile ceylonFile, final TypeChecker typeChecker) {
//        final com.intellij.openapi.module.Module module = ModuleUtil.findModuleForFile(
//                ceylonFile.getVirtualFile(), ceylonFile.getProject());
//        final VirtualFileVirtualFile sourceCodeVirtualFile
//                = new VirtualFileVirtualFile(ceylonFile.getVirtualFile(), module);
//        BaseCeylonProject ceylonProject = sourceCodeVirtualFile.getCeylonProject();
//        if (ceylonProject != null && ! ceylonProject.getTypechecked()) {
//            return null;
//        }
//
//        return doWithSourceModel(ceylonProject, true,
//                0,
//                new Callable<PhasedUnit>() {
//                  @SuppressWarnings("unchecked")
//                @Override
//                  public PhasedUnit call() {
//                      PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);
//                      Tree.CompilationUnit cu = ceylonFile.getCompilationUnit();
//
//                      IdeaVirtualFolder srcDir;
//                      Package pkg;
//
//                      if (phasedUnit == null) {
//                          srcDir = getSourceFolder(ceylonFile, module);
//                          if (srcDir == null) { // happens eg. for *.ceylon files that are not within a source root. Don't do typechecking for these.
//                              return null;
//                          }
//                          pkg = getPackage(sourceCodeVirtualFile, srcDir, typeChecker);
//                      } else {
//                          srcDir = (IdeaVirtualFolder) phasedUnit.getSrcDir();
//                          pkg = phasedUnit.getPackage();
//                      }
//
//                      ProjectPhasedUnit projectPu = null;
//                      if (phasedUnit instanceof EditedPhasedUnit) {
//                          projectPu = ((EditedPhasedUnit) phasedUnit).getOriginalPhasedUnit();
//                      } else if (phasedUnit instanceof ProjectPhasedUnit) {
//                          projectPu = (ProjectPhasedUnit) phasedUnit;
//                      }
//
//                      SingleSourceUnitPackage singleSourceUnitPackage = new SingleSourceUnitPackage(pkg, sourceCodeVirtualFile.getPath());
//
//                      phasedUnit = new EditedPhasedUnit<>(
//                              TypeDescriptor.klass(com.intellij.openapi.module.Module.class),
//                              TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
//                              TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
//                              TypeDescriptor.klass(com.intellij.openapi.vfs.VirtualFile.class),
//                              sourceCodeVirtualFile, srcDir, cu, singleSourceUnitPackage,
//                              typeChecker.getPhasedUnits().getModuleManager(),
//                              (BaseIdeModuleSourceMapper) typeChecker.getPhasedUnits().getModuleSourceMapper(),
//                              typeChecker,
//                              ceylonFile.getTokens(),
//                              projectPu,
//                              module,
//                              ceylonFile.getVirtualFile());
//
//                      BaseIdeModelLoader loader = ((BaseIdeModuleManager) typeChecker.getPhasedUnits()
//                              .getModuleManager()).getModelLoader();
//
//                      loader.loadPackageDescriptors();
//
//                      phasedUnit.validateTree();
//                      phasedUnit.visitSrcModulePhase();
//                      phasedUnit.visitRemainingModulePhase();
//                      phasedUnit.scanDeclarations();
//                      phasedUnit.scanTypeDeclarations();
//                      phasedUnit.validateRefinement();
//                      phasedUnit.analyseTypes();
//                      phasedUnit.analyseUsage();
//                      phasedUnit.analyseFlow();
//
//              /*
//                      typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
//              */
//
//                      ceylonFile.setPhasedUnit(phasedUnit);
//
//                      return phasedUnit;
//                  }
//            });
//    }
//
//    private static IdeaVirtualFolder getSourceFolder(CeylonFile sourceFile, com.intellij.openapi.module.Module mod) {
//        com.intellij.openapi.vfs.VirtualFile root = ProjectRootManager
//                .getInstance(sourceFile.getProject())
//                .getFileIndex()
//                .getSourceRootForFile(sourceFile.getVirtualFile());
//
//        return new IdeaVirtualFolder(root, mod);
//    }
//
//    private static Package getPackage(VirtualFile file, VirtualFile srcDir,
//                                      TypeChecker typeChecker) {
//        Modules modules = typeChecker.getContext().getModules();
//        // Retrieve the target package from the file src-relative path
//        //TODO: this is very fragile!
//        String packageName = constructPackageName(file, srcDir);
//        for (Module module : modules.getListOfModules()) {
//            if (packageName.startsWith(module.getNameAsString())) {
//                Package pkg = module.getDirectPackage(packageName);
//                if (pkg == null) {
//                    pkg = createPackage(packageName, module);
//                }
//                return pkg;
//            }
//        }
//        // Contained in no existing module; assume the default package
//        return modules.getDefaultModule().getPackages().get(0);
//    }
//
//    // createPackage is copied from ModuleManager.
//    private static Package createPackage(String packageName, Module module) {
//        Package pkg;
//        pkg = new Package();
//        List<String> name = packageName.isEmpty() ? Collections.singletonList("") : ModuleManager.splitModuleName(packageName);
//        pkg.setName(name);
//        if (module != null) {
//            module.getPackages().add(pkg);
//            pkg.setModule(module);
//        }
//        return pkg;
//    }
//
//    private static String constructPackageName(com.redhat.ceylon.compiler.typechecker.io.VirtualFile file, com.redhat.ceylon.compiler.typechecker.io.VirtualFile srcDir) {
//        return file.getPath().substring(srcDir.getPath().length() + 1)
//                .replace("/" + file.getName(), "").replace('/', '.');
//    }
//
//    @Override
//    public PhasedUnit typecheck(CeylonFile ceylonFile) {
//        return invokeTypeChecker(ceylonFile);
//    }

    @Override
    public File getEmbeddedCeylonRepository() {
        return CeylonIdePlugin.getEmbeddedCeylonRepository();
    }

    @Override
    public File getSupplementalCeylonRepository() {
        return CeylonIdePlugin.getSupplementalCeylonRepository();
    }

    @Override
    public File getEmbeddedCeylonDist() {
        return CeylonIdePlugin.getEmbeddedCeylonDist();
    }
}
