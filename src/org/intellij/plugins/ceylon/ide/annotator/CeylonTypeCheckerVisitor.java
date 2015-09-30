package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
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
            if (that.getStartIndex() == null || that.getEndIndex() == null) {
                continue;
            }
            TextRange range = new TextRange(that.getStartIndex() + crlfCountDiff, that.getEndIndex() + crlfCountDiff);

            if (that instanceof Tree.Declaration) {
                Tree.Identifier id = ((Tree.Declaration) that).getIdentifier();
                if (id != null && id.getStartIndex() != null && id.getEndIndex() != null) {
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

        AddRemoveAnnotationIntention annotationsFix = new AddRemoveAnnotationIntention(file.getViewProvider().getDocument(), annotation);

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
                // TODO addCreationProposals
                // TODO addChangeReferenceProposals
                break;
            case 101:
                // TODO addCreateParameterProposals
                // TODO addChangeArgumentReferenceProposals
                break;
            case 200:
                // TODO addSpecifyTypeProposal
                break;
            case 300:
                annotation.registerFix(new RefineFormalMembersIntention(that, false));
                annotationsFix.addMakeAbstractDecProposal(that, tc);
                break;
            case 350:
                annotation.registerFix(new RefineFormalMembersIntention(that, true));
                annotationsFix.addMakeAbstractDecProposal(that, tc);
                break;
            case 310:
                annotationsFix.addMakeAbstractDecProposal(that, tc);
                break;
            case 320:
                // TODO addRemoveAnnotationProposal
                break;
            case 400:
            case 402:
                annotationsFix.addMakeSharedProposal(tc, that);
                break;
            case 705:
                annotationsFix.addMakeSharedDecProposal(tc, that);
                break;
            case 500:
            case 510:
                annotationsFix.addMakeDefaultProposal(tc, that);
                break;
            case 600:
                annotationsFix.addMakeActualDecProposal(tc, that);
                break;
            case 701:
                annotationsFix.addMakeSharedDecProposal(tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("actual", tc, that);
                break;
            case 702:
                annotationsFix.addMakeSharedDecProposal(tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("formal", tc, that);
                break;
            case 703:
                annotationsFix.addMakeSharedDecProposal(tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("default", tc, that);
                break;
            case 710:
            case 711:
                annotationsFix.addMakeSharedProposal(tc, that);
                break;
            case 712:
                // TODO addExportModuleImportProposal
                break;
            case 713:
                annotationsFix.addMakeSharedProposalForSupertypes(tc, that);
                break;
            case 714:
                // TODO addExportModuleImportProposalForSupertypes
                break;
            case 800:
            case 804:
                annotationsFix.addMakeVariableProposal(tc, that);
                break;
            case 803:
                annotationsFix.addMakeVariableProposal(tc, that);
                break;
            case 801:
                // TODO never thrown by the type checker?!
                annotationsFix.addMakeVariableDecProposal(tc, cu, that);
                break;
            case 802:
                // TODO never thrown by the type checker?!
                break;
            case 905:
                annotationsFix.addMakeContainerAbstractProposal(tc, that);
                break;
            case 1100:
                annotationsFix.addMakeContainerAbstractProposal(tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("formal", tc, that);
                break;
            case 1101:
                annotationsFix.addRemoveAnnotationDecProposal("formal", tc, that);
                //TODO: replace body with ;
                break;
            case 1000:
            case 1001:
                // TODO addEmptyParameterListProposal(file, proposals, node);
                // TODO addParameterListProposal(file, proposals, node, rootNode);
                // TODO addConstructorProposal(file, proposals, node, rootNode);
                // TODO addChangeDeclarationProposal(problem, file, proposals, node);
                break;
            case 1050:
                // TODO addFixAliasProposal(proposals, file, problem);
                break;
            case 1200:
            case 1201:
                annotationsFix.addRemoveAnnotationDecProposal("shared", tc, that);
                break;
            case 1300:
            case 1301:
                annotationsFix.addMakeRefinedSharedProposal(tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("actual", tc, that);
                break;
            case 1302:
            case 1312:
            case 1307:
                annotationsFix.addRemoveAnnotationDecProposal("formal", tc, that);
                break;
            case 1303:
            case 1313:
            case 1320:
                annotationsFix.addRemoveAnnotationDecProposal("formal", tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("default", tc, that);
                break;
            case 1350:
                annotationsFix.addRemoveAnnotationDecProposal("default", tc, that);
                annotationsFix.addMakeContainerNonfinalProposal(tc, that);
                break;
            case 1400:
            case 1401:
                annotationsFix.addMakeFormalDecProposal(tc, that);
                break;
            case 1450:
                annotationsFix.addMakeFormalDecProposal(tc, that);
                // TODO addParameterProposals(proposals, file, rootNode, node, null);
                // TODO addInitializerProposals(proposals, file, rootNode, node);
                // TODO addParameterListProposal(file, proposals, node, rootNode);
                // TODO addConstructorProposal(file, proposals, node, rootNode);
                break;
            case 1610:
                annotationsFix.addRemoveAnnotationDecProposal("shared", tc, that);
                annotationsFix.addRemoveAnnotationDecProposal("abstract", tc, that);
                break;
            case 1500:
            case 1501:
                annotationsFix.addRemoveAnnotationDecProposal("variable", tc, that);
                break;
            case 1600:
            case 1601:
                annotationsFix.addRemoveAnnotationDecProposal("abstract", tc, that);
                break;
            case 1700:
                annotationsFix.addRemoveAnnotationDecProposal("final", tc, that);
                break;
            case 1800:
            case 1801:
                annotationsFix.addRemoveAnnotationDecProposal("sealed", tc, that);
                break;
            case 1900:
                annotationsFix.addRemoveAnnotationDecProposal("late", tc, that);
                break;
            case 1950:
            case 1951:
                annotationsFix.addRemoveAnnotationDecProposal("annotation", tc, that);
                break;
        }
    }
}
