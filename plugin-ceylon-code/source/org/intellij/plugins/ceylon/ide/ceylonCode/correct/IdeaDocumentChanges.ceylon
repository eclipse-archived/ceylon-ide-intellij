import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaString
}

import com.intellij.openapi.editor {
    Document,
    AliasedAsTextEdit=TextChange
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VirtualFileManager
}
import com.intellij.psi {
    PsiDocumentManager
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.correct {
    DocumentChanges
}

import java.lang {
    CharSequence,
    CharArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared alias TextEdit => AliasedAsTextEdit;

shared class InsertEdit(Integer position, shared String str) satisfies AliasedAsTextEdit {
    shared actual Integer start => position;
    shared actual Integer end => position;
    shared actual CharSequence text => javaString(str);
    shared actual CharArray chars => javaString(str).toCharArray();
}

shared class DeleteEdit(shared actual Integer start, Integer length) satisfies AliasedAsTextEdit {
    shared actual Integer end => start + length;
    shared actual CharSequence text => javaString("");
    shared actual CharArray chars => javaString("").toCharArray();
}

shared class ReplaceEdit(shared actual Integer start, Integer length, String str) satisfies AliasedAsTextEdit {
    shared actual Integer end => start + length;
    shared actual CharSequence text => javaString(str);
    shared actual CharArray chars => javaString(str).toCharArray();
}

shared class TextChange(Document|PhasedUnit|CeylonFile input) {
    value changes = ArrayList<TextEdit>();

    shared Document document;
    if (is Document input) {
        document = input;
    } else {
        VirtualFile? vfile = if (is PhasedUnit input)
                             then VirtualFileManager.instance.findFileByUrl("file://" + input.unit.fullPath)
                             else input.virtualFile;

        assert (exists vfile);

        document = FileDocumentManager.instance.getDocument(vfile);
    }

    shared void addEdit(TextEdit edit) {
        changes.add(edit);
    }
    
    shared void addAll(TextChange change) {
        changes.addAll(change.changes);
    }

    shared void apply(Project? project = null) {
        value markers = changes.collect((c) => document.createRangeMarker(c.start, c.end));
        
        for (change -> marker in zipEntries(changes, markers)) {
            document.replaceString(marker.startOffset, marker.endOffset, change.text);
        }

        if (exists project) {
            PsiDocumentManager.getInstance(project).commitAllDocuments();
        }
    }
    
    shared Boolean hasChildren => !changes.empty;
}

shared interface IdeaDocumentChanges satisfies DocumentChanges<Document, InsertEdit, TextEdit, TextChange> {
    shared actual void initMultiEditChange(TextChange importChange) {
    }

    shared actual Document getDocumentForChange(TextChange change)
            => change.document;

    shared actual TextEdit newDeleteEdit(Integer start, Integer stop)
            => DeleteEdit(start, stop);

    shared actual TextEdit newReplaceEdit(Integer start, Integer stop, String text)
            => ReplaceEdit(start, stop, text);

    shared actual InsertEdit newInsertEdit(Integer position, String text)
            => InsertEdit(position, text);

    shared actual void addEditToChange(TextChange change, TextEdit edit)
            => change.addEdit(edit);

    shared actual String getInsertedText(InsertEdit edit)
            => edit.str;
    
    shared actual Boolean hasChildren(TextChange change)
            => change.hasChildren;
    
    shared actual String getDocContent(Document doc, Integer start, Integer length) {
        return doc.getText(TextRange.from(start, length));
    }
    
    shared actual Integer getLineOfOffset(Document doc, Integer offset)
             => doc.getLineNumber(offset);
    
    shared actual Integer getLineStartOffset(Document doc, Integer line)
            => doc.getLineStartOffset(line);
    
    shared actual String getLineContent(Document doc, Integer line)
            => doc.getText(TextRange.create(
                    doc.getLineStartOffset(line),
                    doc.getLineEndOffset(line)
               ));
}
