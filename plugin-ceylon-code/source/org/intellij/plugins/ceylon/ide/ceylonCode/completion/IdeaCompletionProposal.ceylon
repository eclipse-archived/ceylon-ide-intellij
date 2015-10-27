import ceylon.interop.java {
    javaString
}

import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.completion {
    CommonCompletionProposal
}

import java.lang {
    Character
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import javax.swing {
    Icon
}
import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}

interface IdeaCompletionProposal satisfies CommonCompletionProposal<Document,TextRange> {
    
    shared actual Character getDocChar(Document doc, Integer offset)
            => Character(doc.getText(TextRange.from(offset, 1)).first else ' ');
    
    shared actual Integer getDocLength(Document doc) => doc.textLength;
    
    shared actual String getDocSpan(Document doc, Integer start, Integer length)
            => doc.getText(TextRange.from(start, length));
    
    shared actual TextRange newRegion(Integer start, Integer length) => TextRange.from(start, length);
    shared actual Integer getRegionStart(TextRange region) => region.startOffset;
    shared actual Integer getRegionLength(TextRange region) => region.length;
    
    shared actual void replaceInDoc(Document doc, Integer start, Integer length, String newText) {
        doc.replaceString(start, start + length, javaString(newText));
    }
    
    shared void adjustSelection(CompletionData data) {
        TextRange selection = getSelectionInternal(data.document);
        data.editor.selectionModel.setSelection(selection.startOffset, selection.endOffset);
        data.editor.caretModel.moveToOffset(selection.endOffset);
    }
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
