import com.intellij.codeInsight.completion {
    InsertionContext,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CompletionData data, Package candidate)
        extends ImportedModulePackageProposal<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange, TextRange, IdeaLinkedMode>
        (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => LookupElementBuilder.create(text)
            .withIcon(ideaIcons.packages)
            .withPresentableText(description)
            .withInsertHandler(object satisfies InsertHandler<LookupElement> {
        shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
            // Undo IntelliJ's completion
            replaceInDoc(data.document, offset, text.size - prefix.size, "");
            
            applyInternal(data.document);
        }
    });
    
    shared actual LookupElement newReplacementCompletionResult(Declaration d, TextRange selection, IdeaLinkedMode lm) {
        return LookupElementBuilder.create(d.name)
            .withIcon(ideaIcons.forDeclaration(d));
        // TODO replace selection
    }
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
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