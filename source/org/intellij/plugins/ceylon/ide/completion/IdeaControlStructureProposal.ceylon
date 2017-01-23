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

import org.intellij.plugins.ceylon.ide.util {
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
            }
            .withInsertHandler(CompletionHandler((context) {
                if (exists brace = text.firstOccurrence('{')) {
                    setSelection(ctx, offset + brace + 1 - prefix.size);
                }
            }));

    toggleOverwrite => false;
}
