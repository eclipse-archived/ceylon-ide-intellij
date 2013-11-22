package org.intellij.plugins.ceylon.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
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

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CeylonFile)) {
            return;
        }
        final CeylonFile ceylonFile = (CeylonFile) element;

        TypeCheckerManager manager = ServiceManager.getService(ceylonFile.getProject(), TypeCheckerManager.class);

        TypeChecker typeChecker = manager.getTypeChecker();

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
            srcDir = new FileSystemVirtualFile(new File(ceylonFile.getVirtualFile().getParent().getPath()));
            pkg = typeChecker.getContext().getModules().getDefaultModule().getPackages().get(0);
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
}
