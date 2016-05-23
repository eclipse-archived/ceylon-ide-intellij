import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInspection {
    ProblemHighlightType
}
import com.intellij.lang.annotation {
    AnnotationHolder,
    ExternalAnnotator,
    Annotation
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
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
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaQuickFixData
}
import com.redhat.ceylon.ide.common.correct {
    ideQuickFixManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.openapi.project {
    DumbAware
}

shared class CeylonTypeCheckerAnnotator() 
        extends ExternalAnnotator<CeylonFile?, {[Message, TextRange?]*}?>()
        satisfies DumbAware {
    shared actual CeylonFile? collectInformation(PsiFile file)
            => if (is CeylonFile ceylonFile = file) then ceylonFile else null;
    
    shared actual CeylonFile? collectInformation(PsiFile file, Editor editor, Boolean hasErrors)
            => collectInformation(file);
    
    shared actual {[Message, TextRange?]*}? doAnnotate(CeylonFile ceylonFile)
            => concurrencyManager.withAlternateResolution(() 
                => if (exists pu = ceylonFile.ensureTypechecked(),
                        exists cu = pu.compilationUnit)
                then ErrorsVisitor(cu, ceylonFile).extractMessages()
                else null);
    
    shared actual void apply(PsiFile file, {[Message, TextRange?]*} ceylonMessages, AnnotationHolder holder)
            => concurrencyManager.withAlternateResolution(()
                => ceylonMessages.each(([message,range]) => addAnnotation(message, range, holder)));

    void addAnnotation(Message message, variable TextRange? range, AnnotationHolder annotationHolder) {
        value unresolvedReferenceCodes = [ 100, 102 ];
        value unusedCodes = [ Warning.unusedDeclaration.string, Warning.unusedImport.string ];
        
        Annotation annotation;
        if (message is AnalysisError|RecognitionError|UnexpectedError) {
            annotation = annotationHolder.createErrorAnnotation(range, message.message);
            if (unresolvedReferenceCodes.contains(message.code)) {
                annotation.highlightType = ProblemHighlightType.\iLIKE_UNKNOWN_SYMBOL;
            }
        } else if (is UsageWarning message) {
            annotation = annotationHolder.createWarningAnnotation(range, message.message);
            if (unusedCodes.contains(message.warningName)) {
                annotation.highlightType = ProblemHighlightType.\iLIKE_UNUSED_SYMBOL;
            } else {
                annotation.highlightType = ProblemHighlightType.\iGENERIC_ERROR_OR_WARNING;
            }
        } else {
            annotation = annotationHolder.createInfoAnnotation(range, message.message);
        }
        if (exists r = range,
            !ApplicationManager.application.unitTestMode) {
            addQuickFixes(r, message, annotation, annotationHolder);
        }
    }

    void addQuickFixes(TextRange range, Message error, Annotation annotation, AnnotationHolder annotationHolder) {
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



