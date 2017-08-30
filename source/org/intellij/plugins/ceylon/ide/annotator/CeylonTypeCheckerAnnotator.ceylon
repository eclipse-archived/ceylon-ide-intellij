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
    ModuleUtilCore
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
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
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

import org.intellij.plugins.ceylon.ide.correct {
    IdeaQuickFixData
}
import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProjects,
    concurrencyManager {
        withAlternateResolution
    },
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi
}

shared alias AnyError => AnalysisError|RecognitionError|UnexpectedError;

shared class CeylonTypeCheckerAnnotator() 
        satisfies Annotator & DumbAware {

    value unresolvedReferenceCodes = [ 100, 102, 7000 ];
    value unusedCodes = [
        Warning.unusedDeclaration.string,
        Warning.unusedImport.string
    ];

    Boolean addAnnotation(Message message, TextRange? range,
            AnnotationHolder annotationHolder, PsiFile file, PhasedUnit phasedUnit) {

        if (exists range) {
            value project = file.project;
            value highlighted
                = highlighter.highlightQuotedMessage {
                    description = message.message;
                    project = project;
                }
                .replaceFirst(": ", "<br/>");

            Annotation annotation;
            switch (message)
            case (is AnyError) {
                annotation
                        = annotationHolder.createAnnotation(
                            !message.warning
                                    then HighlightSeverity.error
                                    else HighlightSeverity.warning,
                            range, message.message, highlighted);
                annotation.highlightType
                        = message.code in unresolvedReferenceCodes
                        then ProblemHighlightType.likeUnknownSymbol
                        else ProblemHighlightType.genericErrorOrWarning;
            }
            case (is UsageWarning) {
                annotation
                        = annotationHolder.createAnnotation(
                            HighlightSeverity.warning,
                            range, message.message, highlighted);
                annotation.highlightType
                        = message.warningName in unusedCodes
                        then ProblemHighlightType.likeUnusedSymbol
                        else ProblemHighlightType.genericErrorOrWarning;
            }
            else {
                annotation
                        = annotationHolder.createAnnotation(
                            HighlightSeverity.information,
                            range, message.message, highlighted);
            }

            if (!ApplicationManager.application.unitTestMode) {
                addQuickFixes {
                    range = range;
                    error = message;
                    annotation = annotation;
                    annotationHolder = annotationHolder;
                    phasedUnit= phasedUnit;
                };
            }
        }
        return message is AnyError;
    }

    void addQuickFixes(TextRange range, Message error, Annotation annotation,
            AnnotationHolder annotationHolder, PhasedUnit phasedUnit) {
        assert (is CeylonFile file
                    = annotationHolder.currentAnnotationSession.file,
                exists mod
                    = ModuleUtilCore.findModuleForFile(file.virtualFile, file.project));
        
        if (is IdeaCeylonProjects projects = getCeylonProjects(file.project),
            exists project = projects.getProject(mod),
            exists tc = project.typechecker,
            exists node = nodes.findNode {
                node = phasedUnit.compilationUnit;
                tokens = null;
                startOffset = range.startOffset;
                endOffset = range.endOffset;
            }) {

            assert (exists doc = file.viewProvider.document);
            value data = IdeaQuickFixData {
                message = error;
                nativeDoc = doc;
                rootNode = phasedUnit.compilationUnit;
                phasedUnit = phasedUnit;
                node = node;
                ideaModule = mod;
                annotation = annotation;
                ceylonProject = project;
            };
            
            try {
                if (is UsageWarning error) {
                    ideQuickFixManager.addWarningFixes(data, error);
                } else {
                    ideQuickFixManager.addQuickFixes(data, tc);
                }
            } catch (Exception|AssertionError e) {
                e.printStackTrace();
            }
        }
    }
    
    shared actual void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (is CeylonPsi.CompilationUnitPsi psiElement,
            is CeylonFile ceylonFile = psiElement.containingFile) {
            if (exists pu = ceylonFile.availableAnalysisResult?.typecheckedPhasedUnit) {
                if (exists cu = pu.compilationUnit,
                    !pu is ExternalPhasedUnit) {
                     value ceylonMessages 
                         = ErrorsVisitor(cu, ceylonFile)
                             .extractMessages();

                     /*DaemonCodeAnalyzer.getInstance(ceylonFile.project)
                             .resetImportHintsEnabledForProject();*/
                     
                     variable value hasErrors = false;
                     withAlternateResolution(() {
                         for ([message, range] in ceylonMessages) {
                             value result = addAnnotation {
                                 message = message;
                                 range = range;
                                 annotationHolder = annotationHolder;
                                 file = ceylonFile;
                                 phasedUnit = pu;
                             };
                             hasErrors ||= result;
                         }
                     });

                     if (hasErrors) {
                         WolfTheProblemSolver.getInstance(ceylonFile.project)
                                 .reportProblems(ceylonFile.virtualFile,
                                     object extends ArrayList<Problem>() {
                                         empty => false;
                                     });
                     } else {
                         WolfTheProblemSolver.getInstance(ceylonFile.project)
                                 .clearProblems(ceylonFile.virtualFile);
                     }
                }
            } else {
                platformUtils.log(Status._DEBUG,
                    "The CeylonFile is not typechecked during the GeneralHighlightingPass !");
                platformUtils.newOperationCanceledException();
            }
        }
    }
}
