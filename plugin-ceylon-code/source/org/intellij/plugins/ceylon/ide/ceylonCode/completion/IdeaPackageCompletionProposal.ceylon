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
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
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

class IdeaImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CompletionData data, Package candidate)
        extends ImportedModulePackageProposal<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaLinkedMode,CompletionData>
        (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, data)
        satisfies IdeaDocumentChanges
                & IdeaCompletionProposal
                & IdeaLinkedModeSupport {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.packages,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                replaceInDoc(data.document, offset, text.size - prefix.size, "");
                
                applyInternal(data.document);
                adjustSelection(data);
            }
        }
    );
    
    shared actual LookupElement newPackageMemberCompletionProposal(Declaration d, TextRange selection, IdeaLinkedMode lm) {
        return LookupElementBuilder.create(d.name)
            .withIcon(ideaIcons.forDeclaration(d));
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
        satisfies IdeaDocumentChanges
                & IdeaCompletionProposal
                & IdeaLinkedModeSupport {

    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.modules,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext ctx, LookupElement? t) {
                // TODO
                //ideaModuleImportUtils.addModuleImport(ModuleUtil.findModuleForPsiElement(ctx.file),
                //    data.lastPhasedUnit.\ipackage.\imodule,
                //    version.\imodule,
                //    version.version);

                value selection = getSelectionInternal(ctx.document);
                ctx.editor.selectionModel.setSelection(selection.startOffset,
                    selection.endOffset);
                ctx.editor.caretModel.moveToOffset(selection.endOffset); 
            }
        }
    );

    shared actual String completionMode => "overwrite";
    
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber, Integer exitPosition) {
        lm.buildTemplate(data.editor);
    }
    
    shared actual Boolean toggleOverwrite => false;
}
