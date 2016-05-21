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
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal,
    PackageCompletionProposal
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
    ideaIcons
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}

class IdeaImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CompletionData data, Package candidate)
        extends ImportedModulePackageProposal<LookupElement>
        (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, data)
        satisfies IdeaCompletionProposal
                & IdeaLinkedModeSupport {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.packages,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                value platformDoc = data.commonDocument;
                replaceInDoc(platformDoc, offset, text.size - prefix.size, "");
                
                applyInternal(platformDoc);
                adjustSelection(data);
            }
        }
    );
    
    shared actual LookupElement newPackageMemberCompletionProposal(Declaration d, DefaultRegion selection, LinkedMode lm) {
        return LookupElementBuilder.create(d.name)
            .withIcon(ideaIcons.forDeclaration(d));
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber,
        Integer exitPosition) {
        
        lm.buildTemplate(data.editor);
    }
}

class IdeaQueriedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
    String fullPackageName, CompletionData data, ModuleVersionDetails version, Unit unit,
    ModuleSearchResult.ModuleDetails md)
        extends PackageCompletionProposal
        (offset, prefix, memberPackageSubname, withBody, fullPackageName)
        satisfies IdeaCompletionProposal
                & IdeaLinkedModeSupport {

    shared LookupElement lookupElement => newLookup(description, text, ideaIcons.modules,
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext ctx, LookupElement? t) {
                // TODO
                //ideaModuleImportUtils.addModuleImport(ModuleUtil.findModuleForPsiElement(ctx.file),
                //    data.lastPhasedUnit.\ipackage.\imodule,
                //    version.\imodule,
                //    version.version);
                value selection = getSelectionInternal(data.commonDocument);
                ctx.editor.selectionModel.setSelection(selection.start, selection.end);
                ctx.editor.caretModel.moveToOffset(selection.end); 
            }
        }
    );

    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber, Integer exitPosition) {
        lm.buildTemplate(data.editor);
    }
    
    shared actual Boolean toggleOverwrite => false;
}
