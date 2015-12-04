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
    RefinementCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Reference,
    Declaration,
    Scope
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaDocumentChanges,
    InsertEdit,
    TextEdit,
    TextChange,
    ideaImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaRefinementCompletionProposal(Integer offset, String prefix, Reference pr,
        String desc, String text, CompletionData data,
        Declaration dec, Scope scope, Boolean fullType, Boolean explicitReturnType)
        extends RefinementCompletionProposal<CompletionData,LookupElement,CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaLinkedMode>
        (offset, prefix, pr, desc, text, data, dec, scope, fullType, explicitReturnType) 
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.forDeclaration(dec),
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
                enterLinkedMode(data.document);
            }
        }
    );

    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, 
        Integer start, Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual String completionMode => "overwrite";
    
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals => ideaImportProposals;
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber, Integer exitPosition) {
        lm.buildTemplate(data.editor);
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
    shared actual LookupElement newNestedCompletionProposal(Declaration dec, Integer loc) {
        value unit = data.lastCompilationUnit.unit;
        value name = getNestedCompletionText(false, unit, dec);
        value desc = getNestedCompletionText(true, unit, dec);
        
        return newLookup(desc, name, ideaIcons.forDeclaration(dec));
    }
    
    shared actual LookupElement newNestedLiteralCompletionProposal(String val, Integer loc) {
        return newLookup(val, val, ideaIcons.correction);
    }
    
    shared actual Boolean toggleOverwrite => false;
}