import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.util {
    TextRange
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

    shared LookupElement lookupElement
            => newLookup {
                description = desc;
                text = text;
                icon = icons.correction;
                selection
                        = if (exists brace = text.firstOccurrence('{'))
                        then TextRange.from(start+brace+1, 0) else null;
                /*object handler satisfies InsertHandler<LookupElement> {
                    shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                        // Undo IntelliJ's completion
                        value doc = ctx.commonDocument;
                        if (exists node) {
                            value nodeText = doc.getText {
                                offset = node.startIndex.intValue();
                                length = node.distance.intValue();
                            };

                            replaceInDoc {
                                doc = doc;
                                start = offset;
                                length = text.size - (prefix.size - nodeText.size) + 1;
                                newText = "";
                            };
                            assert (exists project = ctx.editor.project);
                            PsiDocumentManager.getInstance(project)
                                .commitDocument(doc.nativeDocument);
                        }

                        applyInternal(doc);
                        adjustSelection(ctx);
                    }
                }*/
            };
        
    
    shared actual void newNameCompletion(ProposalsHolder proposals, String? name) {
        if (is IdeaProposalsHolder proposals, exists name) {
            proposals.add(LookupElementBuilder.create(name));
        }
    }
    
    toggleOverwrite => false;
}
