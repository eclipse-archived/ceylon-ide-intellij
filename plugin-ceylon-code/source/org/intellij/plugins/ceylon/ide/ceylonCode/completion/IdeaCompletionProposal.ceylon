import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import ceylon.interop.java {
    javaString
}

import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.completion {
    CommonCompletionProposal
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}

import javax.swing {
    Icon
}



shared interface IdeaCompletionProposal satisfies CommonCompletionProposal {
    
    shared actual void replaceInDoc(CommonDocument doc, Integer start, Integer length, String newText) {
        assert(is IdeaDocument doc);
        doc.nativeDocument.replaceString(start, start + length, javaString(newText));
    }
    
    shared void adjustSelection(CompletionData data) {
        value selection = getSelectionInternal(IdeaDocument(data.document));
        data.editor.selectionModel.setSelection(selection.start, selection.end);
        data.editor.caretModel.moveToOffset(selection.end);
    }
    
    shared actual String completionMode => "insert";
}

LookupElementBuilder newLookup(String desc, String text, Icon? icon = null,
    InsertHandler<LookupElement>? handler = null, TextRange? selection = null,
    Object? obj = null) {
    
    variable Integer? cutOffset = null;
    
    Integer? parenOffset = desc.firstOccurrence('(');
    
    if (exists typeOffset = desc.firstOccurrence('<')) {
        if (exists parenOffset, parenOffset < typeOffset) {
            cutOffset = parenOffset;
        } else {
            cutOffset = typeOffset;
        }
    } else if (exists parenOffset) {
        cutOffset = parenOffset;
    }

    String newText = if (exists o = cutOffset) then text.spanTo(o - 1) else text;
    Basic&InsertHandler<LookupElement> newHandler = object satisfies InsertHandler<LookupElement>{
        shared actual void handleInsert(InsertionContext insertionContext, LookupElement? t) {
            if (exists o = cutOffset) {
                insertionContext.document.replaceString(insertionContext.tailOffset,
                    insertionContext.tailOffset, javaString(text.spanFrom(o))); 
            }
            
            if (exists handler) {
                handler.handleInsert(insertionContext, t);
            }
            
            if (exists selection) {
                insertionContext.editor.selectionModel.setSelection(
                    selection.startOffset, selection.endOffset);
                insertionContext.editor.caretModel.moveToOffset(selection.endOffset);
            }
        }
    };
    
    variable LookupElementBuilder builder = LookupElementBuilder.create(obj else text, newText)
            .withPresentableText(desc)
            .withIcon(icon)
            .withInsertHandler(newHandler);
    
    return builder;
}
