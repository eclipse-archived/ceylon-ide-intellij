package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ProjectRootManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs.PsiFileVirtualFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TypeCheckerInvoker {
    public static final Logger LOGGER = Logger.getInstance(CeylonTypeCheckerAnnotator.class);

    /*
     * Mimics com.redhat.ceylon.eclipse.code.parse.CeylonParseController#parse
     */
    public static PhasedUnit invokeTypeChecker(CeylonFile ceylonFile) {
        TypeChecker typeChecker = TypeCheckerProvider.getFor(ceylonFile);

        if (typeChecker == null) {
            return null;
        }
        return invokeTypeChecker(ceylonFile, typeChecker);
    }

    public static PhasedUnit invokeTypeChecker(CeylonFile ceylonFile, TypeChecker typeChecker) {
        PsiFileVirtualFile sourceCodeVirtualFile = new PsiFileVirtualFile(ceylonFile);
        PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);

        CeylonLexer lexer;
        try {
            lexer = new CeylonLexer(new ANTLRInputStream(sourceCodeVirtualFile.getInputStream()));
        } catch (IOException e) {
            LOGGER.error(e);
            throw new RuntimeException("Unexpected error", e);
        }
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        com.redhat.ceylon.compiler.typechecker.io.VirtualFile srcDir;
        Package pkg;

        Tree.CompilationUnit cu;
        CeylonParser parser;
        try {
            parser = new CeylonParser(tokenStream);
            cu = parser.compilationUnit();
        } catch (RecognitionException e) {
            LOGGER.error(e);
            return null;
        }

        for (ParseError error : parser.getErrors()) {
            cu.addParseError(error);
        }
        for (LexError error : lexer.getErrors()) {
            cu.addLexError(error);
        }

        if (phasedUnit == null) {
            srcDir = getSourceFolder(ceylonFile);
            if (srcDir == null) { // happens eg. for *.ceylon files that are not within a source root. Don't do typechecking for these.
                return null;
            }
            pkg = getPackage(sourceCodeVirtualFile, srcDir, typeChecker);
        } else {
            srcDir = phasedUnit.getSrcDir();
            pkg = phasedUnit.getPackage();
        }
        phasedUnit = new PhasedUnit(sourceCodeVirtualFile, srcDir, cu, pkg,
                typeChecker.getPhasedUnits().getModuleManager(),
                typeChecker.getPhasedUnits().getModuleSourceMapper(),
                typeChecker.getContext(),
                tokenStream.getTokens());

//        System.out.printf("Package for %s: %s, %s; module: %s%n", ceylonFile.getName(), ceylonFile.getPackageName(), phasedUnit.getPackage(), phasedUnit.getPackage().getModule());

        phasedUnit.validateTree();
        phasedUnit.visitSrcModulePhase();
        phasedUnit.visitRemainingModulePhase();
        phasedUnit.scanDeclarations();
        phasedUnit.scanTypeDeclarations();
        phasedUnit.validateRefinement();
        phasedUnit.analyseTypes();
        phasedUnit.analyseUsage();
        phasedUnit.analyseFlow();

//        System.out.printf(" -- Package assigned by typechecker: %s in %s%n", phasedUnit.getPackage(), phasedUnit.getPackage().getModule());

        if (typeChecker.getPhasedUnitFromRelativePath(phasedUnit.getPathRelativeToSrcDir()) != null) {
            typeChecker.getPhasedUnits().removePhasedUnitForRelativePath(phasedUnit.getPathRelativeToSrcDir());
        }
        typeChecker.getPhasedUnits().addPhasedUnit(phasedUnit.getUnitFile(), phasedUnit);
        return phasedUnit;
    }

    private static com.redhat.ceylon.compiler.typechecker.io.VirtualFile getSourceFolder(CeylonFile sourceFile) {
        return VFileAdapter.createInstance(ProjectRootManager.getInstance(sourceFile.getProject()).getFileIndex().getSourceRootForFile(sourceFile.getVirtualFile()));
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
        List<String> name = packageName.isEmpty() ? Arrays.asList("") : ModuleManager.splitModuleName(packageName);
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
}
