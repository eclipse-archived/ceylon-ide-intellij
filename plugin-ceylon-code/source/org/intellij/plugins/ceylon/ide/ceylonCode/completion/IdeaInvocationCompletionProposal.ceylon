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
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    InvocationCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Scope,
    Reference
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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaInvocationCompletionProposal(Integer offset, String prefix, String desc, String text, Declaration declaration, Reference? producedReference,
    Scope scope, Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation,
    Boolean qualified, Declaration? qualifyingValue, CompletionData data) 
        extends InvocationCompletionProposal<CompletionData,Module,LookupElement,CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaLinkedMode>
        (offset, prefix, desc, text, declaration, producedReference, scope, data.rootNode, includeDefaulted,
            positionalInvocation, namedInvocation, qualified, qualifyingValue, ideaCompletionManager)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.forDeclaration(declaration),
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                replaceInDoc(data.document, offset, text.size - prefix.size, "");
                PsiDocumentManager.getInstance(data.editor.project).commitDocument(data.document);
                
                value change = TextChange(data.document);
                createChange(change, data.document);

                object extends WriteCommandAction<DefaultRegion?>(data.editor.project, data.file) {
                    shared actual void run(Result<DefaultRegion?> result) {
                        change.apply();
                    }
                }.execute();
                
                adjustSelection(data);
                activeLinkedMode(data.document);
            }
        }
    );
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode(data.editor);
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, Integer start, Integer len,
        Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber,
        Integer exitPosition) {
        
        lm.buildTemplate(data.editor);
    }

    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals 
            => ideaImportProposals;
    
    shared actual String completionMode => "overwrite";
    
}