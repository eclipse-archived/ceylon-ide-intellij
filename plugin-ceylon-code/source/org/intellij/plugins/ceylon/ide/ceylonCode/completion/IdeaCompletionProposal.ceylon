import ceylon.interop.java {
    javaString
}

import com.intellij.codeInsight.completion {
    InsertionContext,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import com.redhat.ceylon.ide.common.completion {
    CommonCompletionProposal
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}


shared interface IdeaCompletionProposal satisfies CommonCompletionProposal {
    
    shared actual void replaceInDoc(CommonDocument doc, Integer start, Integer length, String newText) {
        assert(is IdeaDocument doc);
        doc.nativeDocument.replaceString(start, start + length, javaString(newText));
    }
    
    shared void adjustSelection(IdeaCompletionContext data) {
        value selection = getSelectionInternal(data.commonDocument);
        setSelection(data, selection.start, selection.end);
    }
    
    completionMode => "insert";
}

LookupElementBuilder newLookup(String description, String text, Icon? icon = null)
        => LookupElementBuilder.create(text, text)
            .withPresentableText(description)
            .withIcon(icon);

LookupElementBuilder newDeclarationLookup(String description, String text,
        Referenceable declaration, Icon? icon = null, String? aliasedName = null) {

    value strikeout
            = if (is Declaration declaration)
            then declaration.deprecated
            else false;

    return LookupElementBuilder.create(declaration, aliasedName else declaration.nameAsString)
        .withPresentableText(description)
        .withIcon(icon)
        .withStrikeoutness(strikeout);

}

void setSelection(IdeaCompletionContext data, Integer start, Integer end = start) {
    value editor = data.editor;
    editor.selectionModel.setSelection(start, end);
    editor.caretModel.moveToOffset(end);

}

shared class CompletionHandler(void handle(InsertionContext context))
        satisfies InsertHandler<LookupElement> {
    handleInsert(InsertionContext insertionContext, LookupElement? t)
            => handle(insertionContext);
}
