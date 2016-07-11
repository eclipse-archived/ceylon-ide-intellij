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
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaFunctionCompletionProposal
        (Integer offset, String prefix, String desc, String text, Declaration decl, IdeaCompletionContext ctx)
        extends FunctionCompletionProposal(offset, prefix, desc, text, decl, ctx.lastCompilationUnit)
        satisfies IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement {
        value offset => super.offset;
        return newLookup(desc, text, icons.surround)
            .withInsertHandler(object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? element) {
                // Undo IntelliJ's completion
                value startOfTextToErase = offset;
                value lengthBeforeCaret = ctx.editor.caretModel.offset - startOfTextToErase;
                value platformDoc = ctx.commonDocument;
                replaceInDoc(platformDoc, startOfTextToErase, lengthBeforeCaret, "");

                assert (exists project = ctx.editor.project);
                PsiDocumentManager.getInstance(project).commitDocument(platformDoc.nativeDocument);

                value change = createChange(platformDoc);

                object extends WriteCommandAction<Nothing>(ctx.editor.project, ctx.file) {
                    run(Result<Nothing> result) => change.apply();
                }.execute();

                adjustSelection(ctx);
            }
        });
    }
}
