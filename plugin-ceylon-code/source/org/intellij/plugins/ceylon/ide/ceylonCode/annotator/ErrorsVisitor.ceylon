import ceylon.interop.java {
    javaClass
}
import ceylon.language {
    AssertionError
}

import com.intellij.codeInspection {
    ProblemHighlightType
}
import com.intellij.lang.annotation {
    Annotation,
    AnnotationHolder
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    AnalysisError,
    UsageWarning,
    Warning
}
import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonLexer,
    RecognitionError
}
import com.redhat.ceylon.compiler.typechecker.tree {
    ...
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import java.util {
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaQuickFixData
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.correct {
    ideQuickFixManager
}


"A visitor that visits a compilation unit returned by
 [[com.redhat.ceylon.compiler.typechecker.parser::CeylonParser]] to highlight errors and
  warnings using an [[AnnotationHolder]]."
shared class ErrorsVisitor(AnnotationHolder annotationHolder, CeylonFile file) extends Visitor() {

    value unresolvedReferenceCodes = [ 100, 102 ];
    value unusedCodes = [ Warning.unusedDeclaration.string, Warning.unusedImport.string ];

    shared actual void visitAny(Node that) {
        for (Message error in ArrayList(that.errors)) {
            if (exists start = that.startIndex,
                exists end = that.endIndex) {

                variable TextRange? range = null;
                if (is Tree.Declaration that,
                    exists id = that.identifier,
                    exists idStart = id.startIndex,
                    exists idEnd = id.endIndex) {

                    range = TextRange(id.startIndex.intValue(), id.endIndex.intValue());
                } else {
                    range = TextRange(that.startIndex.intValue(), that.endIndex.intValue());
                }

                if (is RecognitionError error,
                    is CommonToken token = error.recognitionException.token) {

                    if (token.type == CeylonLexer.\iEOF) {
                        // we can't underline EOF, so we try to underline the last word instead
                        variable Integer lastNonWS = file.textLength - 1;
                        while (lastNonWS>=0 && (file.text[lastNonWS]?.whitespace else false)) {
                            lastNonWS --;
                        }
                        variable Integer wordStart = lastNonWS;
                        while (wordStart>=0 && !(file.text[wordStart]?.whitespace else false)) {
                            wordStart --;
                        }
                        if (wordStart<lastNonWS) {
                            range = TextRange(wordStart + 1, lastNonWS + 1);
                        } else {
                            range = TextRange(token.startIndex - 1, token.stopIndex);
                        }
                    } else {
                        range = TextRange(token.startIndex, token.stopIndex + 1);
                    }
                }

                Annotation annotation;
                if (error is AnalysisError|RecognitionError|UnexpectedError) {
                    annotation = annotationHolder.createErrorAnnotation(range, error.message);
                    if (unresolvedReferenceCodes.contains(error.code)) {
                        annotation.highlightType = ProblemHighlightType.\iLIKE_UNKNOWN_SYMBOL;
                    }
                } else if (is UsageWarning error) {
                    annotation = annotationHolder.createWarningAnnotation(range, error.message);
                    if (unusedCodes.contains(error.warningName)) {
                        annotation.highlightType = ProblemHighlightType.\iLIKE_UNUSED_SYMBOL;
                    } else {
                        annotation.highlightType = ProblemHighlightType.\iGENERIC_ERROR_OR_WARNING;
                    }
                } else {
                    annotation = annotationHolder.createInfoAnnotation(range, error.message);
                }

                if (exists r = range,
                    !ApplicationManager.application.unitTestMode) {

                    addQuickFixes(r, error, annotation);
                }
            }
        }
        super.visitAny(that);
    }

    shared actual void handleException(Exception e, Node that) {
        e.printStackTrace();
    }

    void addQuickFixes(TextRange range, Message error, Annotation annotation) {
        assert (is CeylonFile file = annotationHolder.currentAnnotationSession.file);
        value mod = ModuleUtil.findModuleForFile(file.virtualFile, file.project);
        value pu = file.phasedUnit;

        if (is IdeaCeylonProjects projects = file.project.getComponent(javaClass<IdeaCeylonProjects>()),
            exists project = projects.getProject(mod),
            exists tc = project.typechecker,
            exists node = nodes.findNode(pu.compilationUnit, null, range.startOffset, range.endOffset)) {

            value data = IdeaQuickFixData(error, file.viewProvider.document, pu.compilationUnit,
                pu, node, mod, annotation, project);

            try {
                ideQuickFixManager.addQuickFixes(data, tc);
            } catch (Exception|AssertionError e) {
                e.printStackTrace();
            }
        }
    }
}
