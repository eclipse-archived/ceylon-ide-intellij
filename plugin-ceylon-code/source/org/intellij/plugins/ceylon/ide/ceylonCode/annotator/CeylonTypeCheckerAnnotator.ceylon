import com.intellij.codeInspection {
    ProblemHighlightType
}
import com.intellij.lang.annotation {
    AnnotationHolder,
    Annotation,
    HighlightSeverity,
    Annotator
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import com.intellij.openapi.project {
    DumbAware
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.problems {
    WolfTheProblemSolver,
    Problem
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    Warning,
    AnalysisError,
    UsageWarning
}
import com.redhat.ceylon.compiler.typechecker.parser {
    RecognitionError
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Message,
    UnexpectedError
}
import com.redhat.ceylon.ide.common.correct {
    ideQuickFixManager
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import com.redhat.ceylon.ide.common.typechecker {
    ExternalPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaQuickFixData
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    concurrencyManager,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi
}

shared alias AnyError => AnalysisError|RecognitionError|UnexpectedError;

shared class CeylonTypeCheckerAnnotator() 
        satisfies Annotator & DumbAware {

    value unresolvedReferenceCodes = [ 100, 102, 7000 ];
    value unusedCodes = [ Warning.unusedDeclaration.string, Warning.unusedImport.string ];

    Boolean addAnnotation(Message message, TextRange? range, AnnotationHolder annotationHolder, PsiFile file) {

        if (exists range) {
            value project = file.project;
            String highlighted
                = highlighter.highlightQuotedMessage(message.message, project);
            Annotation annotation;
            Boolean isError;
            if (is AnyError message) {
                annotation = annotationHolder.createAnnotation(
                    HighlightSeverity.error,
                    range, message.message, highlighted);
                isError = true;
                if (message.code in unresolvedReferenceCodes) {
                    annotation.highlightType
                        = ProblemHighlightType.likeUnknownSymbol;
                }
            }
            else if (is UsageWarning message) {
                annotation = annotationHolder.createAnnotation(
                    HighlightSeverity.warning,
                    range, message.message, highlighted);
                isError = false;
                if (message.warningName in unusedCodes) {
                    annotation.highlightType
                        = ProblemHighlightType.likeUnusedSymbol;
                }
                else {
                    annotation.highlightType
                        = ProblemHighlightType.genericErrorOrWarning;
                }
            }
            else {
                annotation = annotationHolder.createAnnotation(
                    HighlightSeverity.information,
                    range, message.message, highlighted);
                isError = false;
            }
            if (!ApplicationManager.application.unitTestMode) {
                addQuickFixes {
                    range = range;
                    error = message;
                    annotation = annotation;
                    annotationHolder = annotationHolder;
                };
            }
        }
        return message is AnyError;
    }

    void addQuickFixes(TextRange range, Message error, Annotation annotation, AnnotationHolder annotationHolder) {
        assert (is CeylonFile file = annotationHolder.currentAnnotationSession.file,
                exists mod = ModuleUtil.findModuleForFile(file.virtualFile, file.project),
                exists pu = file.localAnalysisResult.lastPhasedUnit);
        
        if (is IdeaCeylonProjects projects = getCeylonProjects(file.project),
            exists project = projects.getProject(mod),
            exists tc = project.typechecker,
            exists node = nodes.findNode {
                node = pu.compilationUnit;
                tokens = null;
                startOffset = range.startOffset;
                endOffset = range.endOffset;
            }) {

            assert (exists doc = file.viewProvider.document);
            value data = IdeaQuickFixData {
                message = error;
                nativeDoc = doc;
                rootNode = pu.compilationUnit;
                phasedUnit = pu;
                node = node;
                project = mod;
                annotation = annotation;
                ceylonProject = project;
            };
            
            try {
                ideQuickFixManager.addQuickFixes(data, tc);
            } catch (Exception|AssertionError e) {
                e.printStackTrace();
            }
        }
    }
    
    shared actual void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (is CeylonPsi.CompilationUnitPsi psiElement,
            is CeylonFile ceylonFile = psiElement.containingFile) {
            if (exists pu = ceylonFile.upToDatePhasedUnit) {
                if (exists cu = pu.compilationUnit,
                    !pu is ExternalPhasedUnit) {
                     value ceylonMessages 
                         = ErrorsVisitor(cu, ceylonFile)
                             .extractMessages();

                     /*DaemonCodeAnalyzer.getInstance(ceylonFile.project)
                             .resetImportHintsEnabledForProject();*/
                     
                     variable value hasErrors = false;
                     concurrencyManager.withAlternateResolution(
                         () => ceylonMessages.each(
                             ([message,range]) {
                                 value result = addAnnotation {
                                     message = message;
                                     range = range;
                                     annotationHolder = annotationHolder;
                                     file = ceylonFile;
                                 };
                                 hasErrors ||= result;
                             })
                         );

                     if (hasErrors) {
                         value problems = object extends ArrayList<Problem>() {
                             empty => false;
                         };
                         
                         WolfTheProblemSolver.getInstance(ceylonFile.project)
                                 .reportProblems(ceylonFile.virtualFile, problems);
                     } else {
                         WolfTheProblemSolver.getInstance(ceylonFile.project)
                                 .clearProblems(ceylonFile.virtualFile);
                     }
                }
            } else {
                platformUtils.log(Status._DEBUG, "The CeylonFile is not typechecked during the GeneralHighlightinhPass !");
                platformUtils.newOperationCanceledException();
            }
        }
    }
}
