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
    ImportedModulePackageProposal,
    PackageCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration,
    Unit
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
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}

class IdeaImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CompletionData data, Package candidate)
        extends ImportedModulePackageProposal<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange, TextRange, IdeaLinkedMode>
        (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.packages,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                replaceInDoc(data.document, offset, text.size - prefix.size, "");
                
                applyInternal(data.document);
            }
        }
    );
    
    shared actual LookupElement newPackageMemberCompletionProposal(Declaration d, TextRange selection, IdeaLinkedMode lm) {
        return LookupElementBuilder.create(d.name)
            .withIcon(ideaIcons.forDeclaration(d));
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

class IdeaQueriedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
    String fullPackageName, CompletionData data, ModuleVersionDetails version, Unit unit,
    ModuleSearchResult.ModuleDetails md)
        extends PackageCompletionProposal<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange, TextRange, IdeaLinkedMode>
        (offset, prefix, memberPackageSubname, withBody, fullPackageName)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {

    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.modules,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext ctx, LookupElement? t) {
                // TODO add import to module.ceylon!!
                value selection = getSelectionInternal(ctx.document);
                ctx.editor.selectionModel.setSelection(selection.startOffset,
                    selection.endOffset);
                ctx.editor.caretModel.moveToOffset(selection.endOffset); 
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
    
    shared actual Boolean toggleOverwrite => false;
    

}