import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
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
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ControlStructureProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, CompletionData data, Node? node)
        extends ControlStructureProposal<CompletionData,CeylonFile,
        LookupElement,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaLinkedMode>
        (offset, prefix, desc, text, node, declaration, data)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {
    

    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.correction,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                if (exists node) {
                    value start = offset;
                    value nodeText = getDocSpan(data.document, node.startIndex.intValue(), node.distance.intValue());
                    value len = text.size - (prefix.size - nodeText.size) + 1; 

                    replaceInDoc(data.document, start, len, "");
                    PsiDocumentManager.getInstance(data.editor.project).commitDocument(data.document);
                }
                
                applyInternal(data.document);
                adjustSelection(data);
            }
        }
    , null, [declaration, text]);
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, Integer start, Integer len,
        Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber,
        Integer exitPosition) {
        
        // TODO go to exitPosition once we exit linked mode
        lm.buildTemplate(data.editor);
    }
    
    shared actual String completionMode => data.options.completionMode;
    
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,
        TextEdit,TextChange> importProposals => ideaImportProposals;
    
    shared actual LookupElement newNameCompletion(String? name)
            => LookupElementBuilder.create(name);
    
    shared actual Boolean toggleOverwrite => false;
    
}
