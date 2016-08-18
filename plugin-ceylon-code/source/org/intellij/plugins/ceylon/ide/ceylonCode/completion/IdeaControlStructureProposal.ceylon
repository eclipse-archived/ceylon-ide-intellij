import com.intellij.codeInsight.completion {
    InsertionContext,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElement
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
                declaration = declaration;
                object handler satisfies InsertHandler<LookupElement> {
                    shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                        // Undo IntelliJ's completion
                        value doc = ctx.commonDocument;

                        replaceInDoc {
                            doc = doc;
                            start = offset;
                            length = text.size - prefix.size;
                            newText = "";
                        };

                        applyInternal(doc);
                        adjustSelection(ctx);
                    }
                }
            };

    toggleOverwrite => false;
}
