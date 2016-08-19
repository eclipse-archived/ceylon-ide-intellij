import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal,
    PackageCompletionProposal,
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration,
    Unit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname,
        Boolean withBody, String fullPackageName, IdeaCompletionContext ctx, Package candidate)
        extends ImportedModulePackageProposal
        (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, ctx)
        satisfies IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement {
        return newLookup {
            description = description;
            text = text;
            icon = icons.packageArchives;
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
            adjustSelection(ctx);
        }));
    }
    
    shared actual void newPackageMemberCompletionProposal(ProposalsHolder proposals,
            Declaration d, DefaultRegion selection, LinkedMode lm) {
        if (is IdeaProposalsHolder proposals) {
             proposals.add(LookupElementBuilder.create(d.name).withIcon(icons.forDeclaration(d)));
        }
    }
}

class IdeaQueriedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname,
        Boolean withBody, String fullPackageName, IdeaCompletionContext completionCtx,
        ModuleVersionDetails version, Unit unit, ModuleSearchResult.ModuleDetails md)
        extends PackageCompletionProposal
        (offset, prefix, memberPackageSubname, withBody, fullPackageName)
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement {
        return newLookup {
            description = description;
            text = text;
            icon = icons.moduleArchives;
        }
        .withInsertHandler(CompletionHandler((context) {
            // TODO
            //ideaModuleImportUtils.addModuleImport(ModuleUtil.findModuleForPsiElement(ctx.file),
            //    data.lastPhasedUnit.\ipackage.\imodule,
            //    version.\imodule,
            //    version.version);
            value selection = getSelectionInternal(completionCtx.commonDocument);
            context.editor.selectionModel.setSelection(selection.start, selection.end);
            context.editor.caretModel.moveToOffset(selection.end);
        }));
    }
    
    toggleOverwrite => false;
}
