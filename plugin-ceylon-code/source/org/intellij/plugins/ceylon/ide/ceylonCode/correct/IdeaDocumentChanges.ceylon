import com.redhat.ceylon.ide.common.correct {
    DocumentChanges
}
import com.intellij.openapi.editor {
    Document,
    IdeaTextChange = TextChange
}
import java.lang {
    CharSequence,
    CharArray,
    JString = String
}
import ceylon.interop.java {
    javaString
}
import com.intellij.openapi.editor.impl {
    BulkChangesMerger
}
import java.util {
    JList = List,
    ArrayList
}

shared class InsertEdit(Integer position, shared String str) satisfies IdeaTextChange {
    shared actual Integer start => position;
    shared actual Integer end => position;
    shared actual CharSequence text => javaString(str);
    shared actual CharArray chars => javaString(str).toCharArray();
}

shared class DeleteEdit(shared actual Integer start, shared actual Integer end) satisfies IdeaTextChange {
    shared actual CharSequence text => javaString("");
    shared actual CharArray chars => javaString("").toCharArray();
}

shared class ReplaceEdit(shared actual Integer start, Integer length, String str) satisfies IdeaTextChange {
    shared actual Integer end => start + length;
    shared actual CharSequence text => javaString(str);
    shared actual CharArray chars => javaString(str).toCharArray();
}

shared class TextChange(shared Document document) {
    variable JList<IdeaTextChange> changes = ArrayList<IdeaTextChange>();
    
    shared void addEdit(IdeaTextChange edit) {
        changes.add(edit);
    }
    
    shared void apply() {
        value chars = javaString(document.text).toCharArray();
        value newText = BulkChangesMerger.\iINSTANCE.mergeToCharArray(chars, document.textLength, changes);
        document.setText(JString(newText));
    }
}

shared interface IdeaDocumentChanges satisfies DocumentChanges<Document, InsertEdit, IdeaTextChange, TextChange> {
    shared actual void initMultiEditChange(TextChange importChange) {
    }
    
    shared actual Document getDocumentForChange(TextChange change)
            => change.document;
    
    shared actual IdeaTextChange newDeleteEdit(Integer start, Integer stop)
            => DeleteEdit(start, stop);
    
    shared actual IdeaTextChange newReplaceEdit(Integer start, Integer stop, String text)
            => ReplaceEdit(start, stop, text);
    
    shared actual InsertEdit newInsertEdit(Integer position, String text)
            => InsertEdit(position, text);
    
    shared actual void addEditToChange(TextChange change, IdeaTextChange edit)
            => change.addEdit(edit);
    
    shared actual String getInsertedText(InsertEdit edit)
            => edit.str;
}