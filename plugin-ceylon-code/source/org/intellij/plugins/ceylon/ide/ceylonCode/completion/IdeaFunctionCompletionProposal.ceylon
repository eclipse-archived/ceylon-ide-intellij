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
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    FunctionCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges,
    ideaImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

class IdeaFunctionCompletionProposal
        (Integer _offset, String prefix, String desc, String text, Declaration decl, CompletionData data)
        extends FunctionCompletionProposal<LookupElement, CeylonFile, Document, InsertEdit, TextEdit, TextChange, TextRange>
        (_offset, prefix, desc, text, decl, data.rootNode)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(desc, text)
            .withInsertHandler(object satisfies InsertHandler<LookupElement> {
        shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
            // Undo IntelliJ's completion
            value startOfTextToErase = offset;
            value lengthBeforeCaret = data.editor.caretModel.offset - startOfTextToErase; 
            replaceInDoc(data.document, startOfTextToErase, lengthBeforeCaret, "");
            
            PsiDocumentManager.getInstance(data.editor.project).commitDocument(data.document);
            
            value change = TextChange(data.document);
            createChange(change, data.document);
            
            object extends WriteCommandAction<DefaultRegion?>(data.editor.project, data.file) {
                shared actual void run(Result<DefaultRegion?> result) {
                    change.apply();
                }
            }.execute();
            
            adjustSelection(data);
        }
    });
 
    shared actual String completionMode => "overwrite";
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals 
            => ideaImportProposals;
}