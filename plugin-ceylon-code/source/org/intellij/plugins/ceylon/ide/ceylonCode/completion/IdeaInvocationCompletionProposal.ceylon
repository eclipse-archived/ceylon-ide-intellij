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
import com.intellij.openapi.editor {
    Document
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.completion {
    InvocationCompletionProposal
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Scope,
    Reference,
    TypedDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaInvocationCompletionProposal(Integer offset, String prefix, String desc, String text, Declaration declaration, Reference? producedReference,
    Scope scope, Boolean includeDefaulted, Boolean positionalInvocation, Boolean namedInvocation,
    Boolean inherited, Boolean qualified, Declaration? qualifyingValue, CompletionData data)
        extends InvocationCompletionProposal<LookupElement>(offset, prefix, desc, text, declaration, producedReference, scope, data.lastCompilationUnit, includeDefaulted,
    positionalInvocation, namedInvocation, inherited, qualified, qualifyingValue, ideaCompletionManager)
        satisfies IdeaCompletionProposal
                & IdeaLinkedModeSupport {
    
    shared actual variable Boolean toggleOverwrite = false;
    
    String? greyText = " (``declaration.container.qualifiedNameString``)";
    
    String? returnType {
        if (is TypedDeclaration declaration, exists type = declaration.type) {
            return type.asString();
        }
        
        return null;
    }

    shared LookupElement lookupElement => newLookup(desc, text, ideaIcons.forDeclaration(declaration),
        object satisfies InsertHandler<LookupElement> {
            shared actual void handleInsert(InsertionContext? insertionContext, LookupElement? t) {
                // Undo IntelliJ's completion
                value platformDoc = data.commonDocument;
                replaceInDoc(platformDoc, offset, text.size - prefix.size, "");
                PsiDocumentManager.getInstance(data.editor.project).commitDocument(platformDoc.nativeDocument);
                
                value change = createChange(platformDoc);
                
                object extends WriteCommandAction<DefaultRegion?>(data.editor.project, data.file) {
                    shared actual void run(Result<DefaultRegion?> result) {
                        change.apply();
                    }
                }.execute();
                
                adjustSelection(data);
                activeLinkedMode(platformDoc, data);
            }
        }
    , null, [declaration, text])
            .withTailText(greyText, true)
            .withTypeText(returnType);
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner, Integer exitSeqNumber,
        Integer exitPosition) {
        
        lm.buildTemplate(data.editor);
    }
    
    shared actual LookupElement newNestedCompletionProposal(Declaration dec, Declaration? qualifier, Integer loc, Integer index, Boolean basic, String op) {
        value desc = getNestedCompletionText(op, data.lastCompilationUnit.unit, dec, qualifier, basic, true);
        value text = getNestedCompletionText(op, data.lastCompilationUnit.unit, dec, qualifier, basic, false);
        return newLookup(desc, text, ideaIcons.forDeclaration(dec));
    }
    
    shared actual LookupElement newNestedLiteralCompletionProposal(String val, Integer loc, Integer index)
            => newLookup(val, val, ideaIcons.correction);
}
