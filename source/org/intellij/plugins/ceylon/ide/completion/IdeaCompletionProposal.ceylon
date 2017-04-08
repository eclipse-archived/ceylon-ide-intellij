import java.lang {
    Types {
        nativeString
    }
}

import com.intellij.codeInsight.completion {
    InsertionContext,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails
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

import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}


shared interface IdeaCompletionProposal satisfies CommonCompletionProposal {
    
    shared actual void replaceInDoc(CommonDocument doc, Integer start, Integer length, String newText) {
        assert(is IdeaDocument doc);
        doc.nativeDocument.replaceString(start, start + length, nativeString(newText));
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

LookupElementBuilder newModuleLookup(String description, String text,
        ModuleVersionDetails version, Icon? icon = null)
        => LookupElementBuilder.create(version, text)
            .withPresentableText(description)
            .withIcon(icon);

LookupElementBuilder newDeclarationLookup(String description, String text,
        Referenceable declaration, Icon? icon = icons.forDeclaration(declaration),
        String? aliasedName = null, Declaration? qualifyingDeclaration = null) {

    value strikeout
            = if (is Declaration declaration)
            then declaration.deprecated
            else false;

    value lookupString
            = aliasedName
            else declaration.nameAsString;

    value qualifier
            = if (exists qualifyingDeclaration)
            then qualifyingDeclaration.name + "."
            else "";

    value fullLookupString
            = qualifier + lookupString + ":" + text.size.string;

    return LookupElementBuilder.create(declaration, fullLookupString)
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
