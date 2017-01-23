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

import org.intellij.plugins.ceylon.ide.util {
    icons
}

class IdeaInvocationCompletionProposal(Integer offset, String prefix, String desc, String text,
        Declaration declaration, Reference()? producedReference, Scope scope,
        Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation,
        Boolean inherited, Boolean qualified, Declaration? qualifyingDeclaration,
        IdeaCompletionContext ctx)
        extends InvocationCompletionProposal(offset, prefix, desc, text, declaration,
            producedReference, scope, ctx.lastCompilationUnit, includeDefaulted,
            positionalInvocation, namedInvocation, inherited, qualified, qualifyingDeclaration)
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

    shared LookupElement lookupElement
            => newDeclarationLookup {
                description = desc;
                text = text;
                declaration = declaration;
                qualifyingDeclaration = qualifyingDeclaration;
                value aliasedName {
                    value nameInUnit = declaration.getName(ctx.lastCompilationUnit.unit);
                    if (nameInUnit != declaration.name) {
                        return nameInUnit;
                    }
                    for (name in declaration.aliases) {
                        if (desc.startsWith(name.string)) {
                            return name.string;
                        }
                    }
                    return null;
                }
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
                activeLinkedMode(doc, ctx);
            }){})
            .withTailText(greyText, true)
            .withTypeText(returnType);
    
    shared actual void newNestedCompletionProposal(ProposalsHolder proposals,
        Declaration dec, Declaration? qualifier, Integer loc, Integer index, Boolean basic, String op) {
        
        if (is IdeaProposalsHolder proposals) {
            value unit = ctx.lastCompilationUnit.unit;
            
            proposals.add(newLookup {
                description = getNestedCompletionText(op, unit, dec, qualifier, basic, true);
                text = getNestedCompletionText(op, unit, dec, qualifier, basic, false);
                icon = icons.forDeclaration(dec);
            });
        }
    }
    
    shared actual void newNestedLiteralCompletionProposal(ProposalsHolder proposals,
        String val, Integer loc, Integer index) {

        if (is IdeaProposalsHolder proposals) {
            value lookup = newLookup {
                description = val;
                text = val;
                icon = icons.correction;
            };
            switch (val)
            case ("\"\"" | "''") {
                proposals.add(lookup.withInsertHandler(
                    CompletionHandler((context)
                        => context.editor.caretModel
                        .moveToOffset(context.tailOffset - 1))));
            }
            case ("\"\"\"\"\"\"") {
                proposals.add(lookup.withInsertHandler(
                    CompletionHandler((context)
                        => context.editor.caretModel
                        .moveToOffset(context.tailOffset - 3))));
            }
            else if (val.endsWith("{}")) {
                proposals.add(lookup.withInsertHandler(
                    CompletionHandler((context)
                    => context.editor.caretModel
                        .moveToOffset(context.tailOffset - 1))));
            }
            else {
                proposals.add(lookup);
            }
        }
    }
}
