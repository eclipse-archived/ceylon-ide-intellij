import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    FunctionCompletionProposal
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaFunctionCompletionProposal
        (Integer _offset, String prefix, String desc, String text, Declaration decl, IdeaCompletionContext ctx)
        extends FunctionCompletionProposal(_offset, prefix, desc, text, decl, ctx.lastCompilationUnit)
        satisfies IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(desc, text, icons.surround)
            .withInsertHandler(object satisfies InsertHandler<LookupElement> {
        shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
            // Undo IntelliJ's completion
            value startOfTextToErase = offset;
            value lengthBeforeCaret = ctx.editor.caretModel.offset - startOfTextToErase; 
            value platformDoc = ctx.commonDocument;
            replaceInDoc(platformDoc, startOfTextToErase, lengthBeforeCaret, "");
            
            PsiDocumentManager.getInstance(ctx.editor.project).commitDocument(platformDoc.nativeDocument);
            
            value change = createChange(platformDoc);
            
            object extends WriteCommandAction<DefaultRegion?>(ctx.editor.project, ctx.file) {
                shared actual void run(Result<DefaultRegion?> result) {
                    change.apply();
                }
            }.execute();
            
            adjustSelection(ctx);
        }
    });
}
