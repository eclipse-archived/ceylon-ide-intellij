import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ModuleProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
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

class IdeaModuleCompletionProposal(Integer offset, String prefix,
        Integer len, String versioned, ModuleSearchResult.ModuleDetails mod,
        Boolean withBody, ModuleVersionDetails version, 
        String name, Node node, CompletionData data) 
        extends ModuleProposal<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaLinkedMode,CompletionData>
        (offset, prefix, len, versioned, mod, withBody, version, name, node, data)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {
   
    shared LookupElement lookupElement => newLookup(versioned, versioned.spanFrom(len),
       ideaIcons.modules, object satisfies InsertHandler<LookupElement> {
           shared actual void handleInsert(InsertionContext insertionContext,
               LookupElement? t) {
               
               // Undo IntelliJ's completion
               replaceInDoc(data.document, offset, text.size - prefix.size, "");
               
               applyInternal(insertionContext.document);
           }
        }
    );
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, 
       Integer start, Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
       
       lm.addEditableRegion(start, len, proposals);
   }
    
    shared actual String completionMode => "overwrite";
    
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber, Integer exitPosition) {
        lm.buildTemplate(data.editor);
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
    shared actual LookupElement newModuleProposal(ModuleVersionDetails d, TextRange selection, IdeaLinkedMode lm)
            => newLookup(d.version, d.version, null, null, selection); // TODO icon
    
    shared actual Boolean toggleOverwrite => false;
    
    
    
}