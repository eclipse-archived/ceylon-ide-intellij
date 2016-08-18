import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext,
    CompletionInitializationContext {
        selectionEndOffset=SELECTION_END_OFFSET
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
    InvocationCompletionProposal,
    ProposalsHolder
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Scope,
    Reference,
    Value,
    Function,
    ModelUtil
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class IdeaInvocationCompletionProposal(Integer offset, String prefix, String desc, String text,
        Declaration declaration, Reference()? producedReference, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation,
        Boolean inherited, Boolean qualified, Declaration? qualifyingValue,
        IdeaCompletionContext ctx)
        extends InvocationCompletionProposal(offset, prefix, desc, text, declaration,
            producedReference, scope, ctx.lastCompilationUnit, includeDefaulted,
            positionalInvocation, namedInvocation, inherited, qualified, qualifyingValue)
        satisfies IdeaCompletionProposal {
    
    shared actual variable Boolean toggleOverwrite = false;

    value greyText {
        if (qualified) {
            return null;
        } else {
            value containerName = declaration.container.qualifiedNameString;
            //+ (declaration.container is Function then "()" else "");
            return " (``containerName.empty then "default package" else containerName``)";
        }
    }
    
    value returnType {
        if (ModelUtil.isConstructor(declaration)) {
            return "new";
        }
        else {
            return switch (declaration)
            case (is Value)
                declaration.type?.asString()
            case (is Function)
                if (declaration.declaredVoid)
                then "void"
            else declaration.type?.asString()
            else null;
        }
    }

    shared LookupElement lookupElement {
        return newLookup {
                description = desc;
                text = text;
                icon = icons.forDeclaration(declaration);
                deprecated = declaration.deprecated;
                declaration = declaration;
                object handler satisfies InsertHandler<LookupElement> {
                    shared actual void handleInsert(InsertionContext context, LookupElement? t) {
                        // Undo IntelliJ's completion
                        value platformDoc = ctx.commonDocument;
                        value len = context.getOffset(selectionEndOffset) - offset;
                        replaceInDoc(platformDoc, offset, len, "");

                        assert (exists project = ctx.editor.project);
                        PsiDocumentManager.getInstance(project).commitDocument(platformDoc.nativeDocument);

                        value change = createChange(platformDoc);

                        object extends WriteCommandAction<Nothing>(ctx.editor.project, ctx.file) {
                            run(Result<Nothing> result) => change.apply();
                        }.execute();

                        adjustSelection(ctx);
                        activeLinkedMode(platformDoc, ctx);
                    }
                }
            }
            .withTailText(greyText, true)
            .withTypeText(returnType);
    }
    
    shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
        Declaration dec, Declaration? qualifier, Integer loc, Integer index, Boolean basic, String op) {
        
        if (is IdeaProposalsHolder proposals) {
            value unit = ctx.lastCompilationUnit.unit;
            value desc = getNestedCompletionText(op, unit, dec, qualifier, basic, true);
            value text = getNestedCompletionText(op, unit, dec, qualifier, basic, false);
            
            proposals.add(newLookup {
                description = desc;
                text = text;
                icon = icons.forDeclaration(dec);
            });
        }
    }
    
    shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals,
        String val, Integer loc, Integer index) {
        
        if (is IdeaProposalsHolder proposals) {
            proposals.add(newLookup {
                description = val;
                text = val;
                icon = icons.correction;
            });
        }
    }
}
