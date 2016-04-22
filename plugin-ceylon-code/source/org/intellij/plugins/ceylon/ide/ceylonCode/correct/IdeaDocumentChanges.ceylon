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
import com.redhat.ceylon.ide.common.platform {
    PlatformTextChange=TextChange,
    PlatformTextEdit=TextEdit,
    DefaultCompositeChange,
    CommonDocument
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

shared class ReplaceEdit(shared actual Integer start, Integer length, shared String str) satisfies AliasedAsTextEdit {
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

shared interface IdeaDocumentChanges satisfies DocumentChanges<Document, InsertEdit, AliasedAsTextEdit, TextChange> {
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

    shared actual String getInsertedText(TextEdit edit)
            => switch(edit)
               case(is InsertEdit) edit.str
               case(is ReplaceEdit) edit.str
               else "";
    
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

shared class DocumentWrapper(shared Document doc) satisfies CommonDocument {
    getLineOfOffset(Integer offset) => doc.getLineNumber(offset);

    getLineContent(Integer line)
            => doc.getText(TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line)));

    getLineEndOffset(Integer line) => doc.getLineEndOffset(line);

    getLineStartOffset(Integer line) => doc.getLineStartOffset(line);

    getText(Integer offset, Integer length) => doc.getText(TextRange.from(offset, length));

    getDefaultLineDelimiter() => "\n";
}

shared class IdeaTextChange(CommonDocument|PhasedUnit|CeylonFile input) satisfies PlatformTextChange {

    value changes = ArrayList<PlatformTextEdit>();

    shared Document doc;
    if (is CommonDocument input) {
        assert (is DocumentWrapper input);
        doc = input.doc;
    } else {
        VirtualFile? vfile = if (is PhasedUnit input)
        then VirtualFileManager.instance.findFileByUrl("file://" + input.unit.fullPath)
        else input.virtualFile;

        assert (exists vfile);

        doc = FileDocumentManager.instance.getDocument(vfile);
    }

    addEdit(PlatformTextEdit edit) => changes.add(edit);

    shared actual CommonDocument document = DocumentWrapper(doc);

    hasEdits => !changes.empty;

    shared actual void initMultiEdit() {}

    shared void apply(Project? project = null) {
        value markers = changes.collect(
            (c) => doc.createRangeMarker(c.start, c.start + c.length)
        );

        for (change -> marker in zipEntries(changes, markers)) {
            doc.replaceString(marker.startOffset, marker.endOffset, javaString(change.text));
        }

        if (exists project) {
            PsiDocumentManager.getInstance(project).commitAllDocuments();
        }
    }
}

shared class IdeaCompositeChange() extends DefaultCompositeChange("") {

    shared void applyChanges(Project myProject)
            => changes.narrow<IdeaTextChange>().map((_) => _.apply(myProject));
}
