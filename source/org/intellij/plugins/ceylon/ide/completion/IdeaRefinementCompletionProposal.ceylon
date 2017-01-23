import com.intellij.codeInsight.completion {
    CompletionInitializationContext {
        selectionEndOffset=\iSELECTION_END_OFFSET
    }
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    RefinementCompletionProposal,
    ProposalsHolder
}
import com.redhat.ceylon.model.typechecker.model {
    Reference,
    Declaration,
    Scope
}

import org.intellij.plugins.ceylon.ide.util {
    icons
}

class IdeaRefinementCompletionProposal(Integer offset, String prefix, Reference pr,
        String desc, String text, IdeaCompletionContext ctx, Declaration dec, Scope scope,
        Boolean fullType, Boolean explicitReturnType, String? returnType)
        extends RefinementCompletionProposal
        (offset, prefix, pr, desc, text, ctx, dec, scope, fullType, explicitReturnType) 
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement
            => newDeclarationLookup {
                description = desc;
                text = text;
                declaration = dec;
                icon = if (explicitReturnType) //only true for "real" refinements
                    then (dec.formal then icons.refinement else icons.extendedType)
                    else icons.param; //named argument
            }
            .withInsertHandler(
                //use a new class here since LookupElement's equals() method depends on it
                object extends CompletionHandler((context) {
                // Undo IntelliJ's completion
                value doc = ctx.commonDocument;

                replaceInDoc {
                    doc = doc;
                    start = offset;
                    length = context.getOffset(selectionEndOffset) - offset;
                    newText = "";
                };

                assert (exists proj = ctx.editor.project);
                PsiDocumentManager.getInstance(proj)
                    .commitDocument(doc.nativeDocument);

                value change = createChange(doc);

                object extends WriteCommandAction<Nothing>(proj, ctx.file) {
                    run(Result<Nothing> result) => change.apply();
                }.execute();

                adjustSelection(ctx);
                enterLinkedMode(doc);
            }){})
            .withTypeText(returnType);
    
    shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
        Declaration dec, Integer loc) {
        value unit = ctx.lastCompilationUnit.unit;
        
        if (is IdeaProposalsHolder proposals) {
            proposals.add(newLookup {
                description = getNestedCompletionText(true, unit, dec);
                text = getNestedCompletionText(false, unit, dec);
                icon = icons.forDeclaration(dec);
            });
        }
    }
    
    shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals,
        String val, Integer loc) {

        if (is IdeaProposalsHolder proposals) {
            proposals.add(newLookup {
                description = val;
                text = val;
                icon = icons.correction;
            });
        }
    }
    
    toggleOverwrite => false;
}
