import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node
}
import org.eclipse.ceylon.ide.common.completion {
    ControlStructureProposal
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.ceylon.ide.intellij.util {
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
