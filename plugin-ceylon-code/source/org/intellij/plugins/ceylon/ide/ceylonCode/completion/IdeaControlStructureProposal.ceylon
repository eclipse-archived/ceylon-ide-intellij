import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ControlStructureProposal,
    ProposalsHolder
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, IdeaCompletionContext ctx, Node? node)
        extends ControlStructureProposal
        (offset, prefix, desc, text, node, declaration, ctx)
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement => newLookup(desc, text, icons.correction,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                value platformDoc = ctx.commonDocument;
                if (exists node) {
                    value start = offset;
                    value nodeText = platformDoc.getText(node.startIndex.intValue(), node.distance.intValue());
                    value len = text.size - (prefix.size - nodeText.size) + 1; 

                    replaceInDoc(platformDoc, start, len, "");
                    PsiDocumentManager.getInstance(ctx.editor.project).commitDocument(platformDoc.nativeDocument);
                }
                
                applyInternal(platformDoc);
                adjustSelection(ctx);
            }
        }
    , null, [declaration, text]);
        
    
    shared actual void newNameCompletion(ProposalsHolder proposals, String? name) {
        if (is IdeaProposalsHolder proposals) {
            proposals.add(LookupElementBuilder.create(name));
        }
    }
    
    shared actual Boolean toggleOverwrite => false;
}
