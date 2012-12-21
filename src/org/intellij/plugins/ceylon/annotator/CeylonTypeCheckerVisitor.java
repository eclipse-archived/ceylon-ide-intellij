package org.intellij.plugins.ceylon.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

class CeylonTypeCheckerVisitor extends Visitor {

    private AnnotationHolder annotationHolder;

    public CeylonTypeCheckerVisitor(AnnotationHolder annotationHolder) {
        this.annotationHolder = annotationHolder;
    }

    public void accept(@NotNull PsiFile file) {
        TypeCheckerManager manager = ServiceManager.getService(file.getProject(), TypeCheckerManager.class);

        TypeChecker typeChecker = manager.getTypeChecker();

        SourceCodeVirtualFile sourceCodeVirtualFile = new SourceCodeVirtualFile(file);
        PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);

        CeylonLexer lexer = null;
        try {
            lexer = new CeylonLexer(new ANTLRInputStream(sourceCodeVirtualFile.getInputStream()));
        } catch (IOException e) {
            Logger.getInstance(CeylonTypeCheckerVisitor.class).error(e);
        }
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        CeylonParser parser = new CeylonParser(tokenStream);

        Tree.CompilationUnit cu = null;
        try {
            cu = parser.compilationUnit();
        } catch (RecognitionException e) {
            Logger.getInstance(CeylonTypeCheckerVisitor.class).error(e);
        }

        VirtualFile srcDir = (phasedUnit == null) ? new FileSystemVirtualFile(new File(file.getVirtualFile().getParent().getPath())) : phasedUnit.getSrcDir();
        com.redhat.ceylon.compiler.typechecker.model.Package pkg = (phasedUnit == null) ?
                typeChecker.getContext().getModules().getDefaultModule().getPackages().get(0) : phasedUnit.getPackage();

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

        if (phasedUnit.getCompilationUnit() == null) {
            return;
        }

        phasedUnit.getCompilationUnit().visit(this);
    }

    @Override
    public void visitAny(Node that) {
        for (Message error : that.getErrors()) {
            int crlfCountDiff = 0; //SystemInfo.isWindows ? (error.getLine() - 1) * 2 : 0;
            TextRange range = new TextRange(that.getStartIndex() + crlfCountDiff, that.getStopIndex() + crlfCountDiff + 1);

            if (that instanceof Tree.Declaration) {
                Tree.Identifier id = ((Tree.Declaration) that).getIdentifier();
                range = new TextRange(id.getStartIndex() - crlfCountDiff, id.getStopIndex() - crlfCountDiff + 1);
            }

            if (error instanceof AnalysisError) {
                Annotation annotation = annotationHolder.createErrorAnnotation(range, error.getMessage());

                if (error.getCode() == 102) {
                    annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            } else if (error instanceof UsageWarning) {
                Annotation annotation = annotationHolder.createWarningAnnotation(range, error.getMessage());
                annotation.setHighlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            } else {
                annotationHolder.createInfoAnnotation(range, error.getMessage());
            }
        }
        super.visitAny(that);
    }
}
