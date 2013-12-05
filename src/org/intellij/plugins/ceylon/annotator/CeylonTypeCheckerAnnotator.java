package org.intellij.plugins.ceylon.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.injected.InjectedFileViewProvider;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CeylonTypeCheckerAnnotator implements Annotator {

    public static final Logger LOGGER = Logger.getInstance(CeylonTypeCheckerAnnotator.class);

    /*
     * Mimics com.redhat.ceylon.eclipse.code.parse.CeylonParseController#parse
     */
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CeylonFile)) {
            return;
        }
        if (((CeylonFile) element).getViewProvider() instanceof InjectedFileViewProvider) {
            return; // Most likely highlighting in backticks, so we skip it
        }
        final CeylonFile ceylonFile = (CeylonFile) element;

        TypeCheckerManager manager = ServiceManager.getService(ceylonFile.getProject(), TypeCheckerManager.class);

        TypeChecker typeChecker = manager.getTypeChecker();

        if (typeChecker == null) {
            return; // not ready yet, the annotator will be called again once the type checker is instantiated
        }
        SourceCodeVirtualFile sourceCodeVirtualFile = new SourceCodeVirtualFile(ceylonFile);
        PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);

        CeylonLexer lexer = null;
        try {
            lexer = new CeylonLexer(new ANTLRInputStream(sourceCodeVirtualFile.getInputStream()));
        } catch (IOException e) {
            LOGGER.error(e);
        }
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        VirtualFile srcDir;
        com.redhat.ceylon.compiler.typechecker.model.Package pkg;
        Tree.CompilationUnit cu = ceylonFile.getCompilationUnit();
        if (cu == null) {
            try {
                CeylonParser parser = new CeylonParser(tokenStream);
                cu = parser.compilationUnit();
            } catch (RecognitionException e) {
                LOGGER.error(e);
            }
        }
        if (phasedUnit == null) {
            srcDir = getSourceFolder(ceylonFile);
            pkg = getPackage(sourceCodeVirtualFile, srcDir, phasedUnit, typeChecker);
        } else {
            srcDir = phasedUnit.getSrcDir();
            pkg = phasedUnit.getPackage();
        }
        phasedUnit = new PhasedUnit(sourceCodeVirtualFile, srcDir, cu, pkg,
                typeChecker.getPhasedUnits().getModuleManager(), typeChecker.getContext(), tokenStream.getTokens());

        phasedUnit.validateTree();
        phasedUnit.visitSrcModulePhase();
        phasedUnit.visitRemainingModulePhase();
        phasedUnit.scanDeclarations();
        phasedUnit.scanTypeDeclarations();
        phasedUnit.validateRefinement();
        phasedUnit.analyseTypes();
        phasedUnit.analyseUsage();
        phasedUnit.analyseFlow();

        if (typeChecker.getPhasedUnitFromRelativePath(phasedUnit.getPathRelativeToSrcDir()) != null) {
            typeChecker.getPhasedUnits().removePhasedUnitForRelativePath(phasedUnit.getPathRelativeToSrcDir());
        }
        typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);

        if (phasedUnit.getCompilationUnit() != null) {
            phasedUnit.getCompilationUnit().visit(new CeylonTypeCheckerVisitor(holder));
        }
    }

    private VirtualFile getSourceFolder(CeylonFile sourceFile) {
        com.intellij.openapi.vfs.VirtualFile sourceRoot = sourceFile.getVirtualFile().getParent();

        if (sourceRoot == null) {
            return null;
        }

        return new FileSystemVirtualFile(new File(sourceRoot.getPath()));
    }

    private Package getPackage(VirtualFile file, VirtualFile srcDir,
                               PhasedUnit builtPhasedUnit, TypeChecker typeChecker) {
        Package pkg = null;
        if (builtPhasedUnit != null) {
            // Editing an already built file
            Package sourcePackage = builtPhasedUnit.getPackage();
//            if (sourcePackage instanceof LazyPackage) {
//                JDTModelLoader modelLoader = getProjectModelLoader(getProject());
//                if (modelLoader != null) {
//                    pkg = new LazyPackage(modelLoader);
//                }
//                else {
//                    pkg = new Package();
//                }
//            }
//            else {
            pkg = new Package();
//            }

            pkg.setName(sourcePackage.getName());
            pkg.setModule(sourcePackage.getModule());
            for (Unit pkgUnit : sourcePackage.getUnits()) {
                pkg.addUnit(pkgUnit);
            }
        } else {
            // Editing a new file
            Modules modules = typeChecker.getContext().getModules();
            // Retrieve the target package from the file src-relative path
            //TODO: this is very fragile!
            String packageName = constructPackageName(file, srcDir);
            for (Module module : modules.getListOfModules()) {
                for (Package p : module.getPackages()) {
                    if (p.getQualifiedNameString().equals(packageName)) {
                        pkg = p;
                        break;
                    }
                }
                if (pkg != null) {
                    break;
                }
            }
            if (pkg == null) {
                // assume the default package
                pkg = modules.getDefaultModule().getPackages().get(0);

                // TODO : iterate through parents to get the sub-package
                // in which the package has been created, until we find the module
                // Then the package can be created.
                // However this should preferably be done on notification of the
                // resource creation
                // A more global/systematic integration between the model element
                // (modules, packages, Units) and the IResourceModel should
                // maybe be considered. But for now it is not required.
            }
        }
        return pkg;
    }

    private String constructPackageName(VirtualFile file, VirtualFile srcDir) {
        return file.getPath().substring(srcDir.getPath().length() + 1)
                .replace("/" + file.getName(), "").replace('/', '.');
    }
}
