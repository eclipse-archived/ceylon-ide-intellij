import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.completion {
    ModuleProposal,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaModuleCompletionProposal(Integer offset, String prefix,
        Integer len, String versioned, ModuleSearchResult.ModuleDetails mod,
        Boolean withBody, ModuleVersionDetails version,
        String name, Node node, IdeaCompletionContext ctx)
        extends ModuleProposal
        (offset, prefix, len, versioned, mod, withBody, version, name, node, ctx)
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement {
        return newLookup {
            description = versioned;
            text = versioned.spanFrom(len);
            icon = icons.moduleArchives;
        }
        .withInsertHandler(CompletionHandler((context) {
           // Undo IntelliJ's completion
           value doc = ctx.commonDocument;

           replaceInDoc {
               doc = doc;
               start = offset;
               length = text.size - prefix.size;
               newText = "";
           };

           applyInternal(doc);
        }));
    }

    shared actual void newModuleProposal(ProposalsHolder proposals,
            ModuleVersionDetails details, DefaultRegion selection, LinkedMode linkedMode) {
        if (is IdeaProposalsHolder proposals) {
            // TODO icon
            value version = details.version;
            proposals.add(newLookup {
                description = version;
                text = version;
                icon = null;
            }
            .withInsertHandler(CompletionHandler((context)
                    => setSelection(ctx, selection.start, selection.end))));
        }
    }

    toggleOverwrite => false;
}
