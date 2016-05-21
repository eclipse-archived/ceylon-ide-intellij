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
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ControlStructureProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}


import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, CompletionData data, Node? node)
        extends ControlStructureProposal<LookupElement>
        (offset, prefix, desc, text, node, declaration, data)
        satisfies IdeaCompletionProposal
                & IdeaLinkedModeSupport {

    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.correction,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                value platformDoc = data.commonDocument;
                if (exists node) {
                    value start = offset;
                    value nodeText = platformDoc.getText(node.startIndex.intValue(), node.distance.intValue());
                    value len = text.size - (prefix.size - nodeText.size) + 1; 

                    replaceInDoc(platformDoc, start, len, "");
                    PsiDocumentManager.getInstance(data.editor.project).commitDocument(platformDoc.nativeDocument);
                }
                
                applyInternal(platformDoc);
                adjustSelection(data);
            }
        }
    , null, [declaration, text]);
        
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber,
        Integer exitPosition) {
        
        // TODO go to exitPosition once we exit linked mode
        lm.buildTemplate(data.editor);
    }
    
    shared actual LookupElement newNameCompletion(String? name)
            => LookupElementBuilder.create(name);
    
    shared actual Boolean toggleOverwrite => false;
}
