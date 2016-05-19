import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaString
}

import com.intellij.openapi.editor {
    Document
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
import com.redhat.ceylon.ide.common.platform {
    PlatformTextChange=TextChange,
    PlatformTextEdit=TextEdit,
    DefaultCompositeChange,
    CommonDocument
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}


shared class DocumentWrapper(shared Document doc) satisfies CommonDocument {
    getLineOfOffset(Integer offset) => doc.getLineNumber(offset);

    getLineContent(Integer line)
            => doc.getText(TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line)));

    getLineEndOffset(Integer line) => doc.getLineEndOffset(line);

    getLineStartOffset(Integer line) => doc.getLineStartOffset(line);

    getText(Integer offset, Integer length) => doc.getText(TextRange.from(offset, length));

    defaultLineDelimiter => "\n";

    indentSpaces => 4;

    indentWithSpaces => true;

    size => doc.textLength;
}

shared class IdeaTextChange(CommonDocument|PhasedUnit|CeylonFile input) satisfies PlatformTextChange {

    value edits = ArrayList<PlatformTextEdit>();

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

    addEdit(PlatformTextEdit edit) => edits.add(edit);

    shared actual CommonDocument document = DocumentWrapper(doc);

    hasEdits => !edits.empty;

    shared actual void initMultiEdit() {}

    shared actual void apply() => applyOnProject();

    shared void applyOnProject(Project? project = null) {
        value markers = edits.collect(
            (c) => doc.createRangeMarker(c.start, c.start + c.length)
        );

        for (change -> marker in zipEntries(edits, markers)) {
            doc.replaceString(marker.startOffset, marker.endOffset, javaString(change.text));
        }

        if (exists project) {
            PsiDocumentManager.getInstance(project).commitAllDocuments();
        }
    }

    offset => if (exists e = edits.first) then e.start else 0;
    length => if (exists e = edits.first) then e.length else 0;
}

shared class IdeaCompositeChange() extends DefaultCompositeChange("") {

    shared void applyChanges(Project myProject)
            => changes.narrow<IdeaTextChange>().each((_) => _.applyOnProject(myProject));
}
