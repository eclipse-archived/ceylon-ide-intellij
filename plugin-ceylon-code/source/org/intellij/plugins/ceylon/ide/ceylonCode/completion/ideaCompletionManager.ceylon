import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet
}
import com.intellij.util {
    ProcessingContext
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.completion {
    completionManager
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    DummyProgressMonitor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}


shared object ideaCompletionManager  {
    
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result,
            PhasedUnit pu, TypeChecker tc, CompletionOptions options) {
        value isSecondLevel = parameters.invocationCount > 0 && parameters.invocationCount % 2 == 0;
        value element = parameters.originalPosition;
        value doc = parameters.editor.document;
        assert(is CeylonFile ceylonFile = element.containingFile);
        value params = IdeaCompletionContext(ceylonFile, parameters.editor, nothing, options);
        value line = doc.getLineNumber(element.textOffset);
        
        value monitor = DummyProgressMonitor.wrap("");
        value returnedParamInfo = true; // The parameters tooltip has nothing to do with code completion, so we bypass it
        completionManager.getContentProposals(pu.compilationUnit, params, 
            parameters.editor.caretModel.offset, line, isSecondLevel, monitor, returnedParamInfo);
        
        CustomLookupCellRenderer.install(parameters.editor.project);
        
        for (completion in params.proposals.proposals) {
            result.addElement(completion);
        }
        
        if (!isSecondLevel) {
            result.addLookupAdvertisement("Call again to toggle second-level completions");
        }
    }
}
