package org.intellij.plugins.ceylon.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import org.jetbrains.annotations.NotNull;

class CeylonTypeCheckerVisitor extends Visitor {

    private AnnotationHolder annotationHolder;

    public CeylonTypeCheckerVisitor(AnnotationHolder annotationHolder) {
        this.annotationHolder = annotationHolder;
    }

    public void accept(@NotNull PsiFile file) {
        TypeCheckerManager manager = ServiceManager.getService(file.getProject(), TypeCheckerManager.class);

        TypeChecker typeChecker = manager.getTypeChecker();

        PhasedUnit phasedUnit = typeChecker.getPhasedUnit(new SourceCodeVirtualFile(file));

        phasedUnit.analyseTypes();
        phasedUnit.analyseUsage();

        if (phasedUnit == null || phasedUnit.getCompilationUnit() == null) {
            return;
        }

        phasedUnit.getCompilationUnit().visit(this);
    }

    @Override
    public void visitAny(Node that) {
        for (Message error : that.getErrors()) {
            int numNL = SystemInfo.isWindows ? error.getLine() - 1 : 0;
            TextRange range = new TextRange(that.getStartIndex() - numNL, that.getStopIndex() - numNL + 1);

            if (that instanceof Tree.Declaration) {
                Tree.Identifier id = ((Tree.Declaration) that).getIdentifier();
                range = new TextRange(id.getStartIndex() - numNL, id.getStopIndex() - numNL + 1);
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
