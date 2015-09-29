package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.analyzer.UsageWarning;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import org.apache.commons.lang.ArrayUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.correct.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor that visits a compilation unit returned by {@link com.redhat.ceylon.compiler.typechecker.parser.CeylonParser}
 * to highlight errors and warnings using an {@link com.intellij.lang.annotation.AnnotationHolder}.
 */
class CeylonTypeCheckerVisitor extends Visitor {

    private AnnotationHolder annotationHolder;

    private static final int[] UNRESOLVED_REFERENCE_CODES = {100, 102};

    /**
     * Creates a new visitor that will report errors and warnings in {@code annotationHolder}.
     *
     * @param annotationHolder the receiver of the annotations
     */
    public CeylonTypeCheckerVisitor(AnnotationHolder annotationHolder) {
        this.annotationHolder = annotationHolder;
    }

    @Override
    public void visitAny(Node that) {
        for (Message error : that.getErrors()) {
            int crlfCountDiff = 0; //SystemInfo.isWindows ? (error.getLine() - 1) * 2 : 0;
            TextRange range = new TextRange(that.getStartIndex() + crlfCountDiff, that.getEndIndex() + crlfCountDiff);

            if (that instanceof Tree.Declaration) {
                Tree.Identifier id = ((Tree.Declaration) that).getIdentifier();
                if (id != null) {
                    range = new TextRange(id.getStartIndex() - crlfCountDiff, id.getEndIndex() - crlfCountDiff);
                }
            }

            Annotation annotation;
            if (error instanceof AnalysisError) {
                annotation = annotationHolder.createErrorAnnotation(range, error.getMessage());

                if (ArrayUtils.contains(UNRESOLVED_REFERENCE_CODES, error.getCode())) {
                    annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            } else if (error instanceof UsageWarning) {
                annotation = annotationHolder.createWarningAnnotation(range, error.getMessage());
                annotation.setHighlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            } else {
                annotation = annotationHolder.createInfoAnnotation(range, error.getMessage());
            }

            addQuickFixes(that, error, annotation);
        }
        super.visitAny(that);
    }

    private void addQuickFixes(Node that, Message error, Annotation annotation) {
        CeylonFile file = (CeylonFile) annotationHolder.getCurrentAnnotationSession().getFile();
        Tree.CompilationUnit cu = file.getCompilationUnit();
        TypeChecker tc = TypeCheckerProvider.getFor(file);

        switch (error.getCode()) {
            case 100:
                annotation.registerFix(new DeclareLocalIntention(cu, that, file.getProject()));
                // fall-through
            case 102:
                if (tc != null) {
                    List<LookupElement> proposals = new ArrayList<>();
                    ideaImportProposals_.get_().addImportProposals(cu, that, proposals, file);
                    for (LookupElement proposal : proposals) {
                        annotation.registerFix(new ImportTypeIntention(proposal));
                    }
                }
                new CreateEnumIntention(file.getViewProvider().getDocument(), annotation)
                        .addCreateEnumProposal(cu, that, tc);
                break;
            case 300:
                annotation.registerFix(new RefineFormalMembersIntention(that, false));
                break;
            case 350:
                annotation.registerFix(new RefineFormalMembersIntention(that, true));
                break;
        }
    }
}
