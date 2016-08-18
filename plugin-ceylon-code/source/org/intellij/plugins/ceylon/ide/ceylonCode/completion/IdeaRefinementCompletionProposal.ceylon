import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
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

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaRefinementCompletionProposal(Integer offset, String prefix, Reference pr,
        String desc, String text, IdeaCompletionContext ctx, Declaration dec, Scope scope,
        Boolean fullType, Boolean explicitReturnType)
        extends RefinementCompletionProposal
        (offset, prefix, pr, desc, text, ctx, dec, scope, fullType, explicitReturnType) 
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement {
        return newLookup {
                description = desc;
                text = text;
                icon = dec.formal then icons.refinement else icons.extendedType;
                deprecated = dec.deprecated;
                object handler satisfies InsertHandler<LookupElement> {
                    shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                        // Undo IntelliJ's completion
                        value platformDoc = ctx.commonDocument;
                        replaceInDoc(platformDoc, offset, text.size - prefix.size, "");

                        assert (exists project = ctx.editor.project);
                        PsiDocumentManager.getInstance(project).commitDocument(platformDoc.nativeDocument);

                        value change = createChange(platformDoc);

                        object extends WriteCommandAction<Nothing>(ctx.editor.project, ctx.file) {
                            run(Result<Nothing> result) => change.apply();
                        }.execute();

                        adjustSelection(ctx);
                        enterLinkedMode(platformDoc);
                    }
                }
            };
    }
    
    shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
        Declaration dec, Integer loc) {
        value unit = ctx.lastCompilationUnit.unit;
        value name = getNestedCompletionText(false, unit, dec);
        value desc = getNestedCompletionText(true, unit, dec);
        
        if (is IdeaProposalsHolder proposals) {
            proposals.add(newLookup {
                description = desc;
                text = name;
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
