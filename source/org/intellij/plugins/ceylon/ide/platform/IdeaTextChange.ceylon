import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.intellij.openapi.project {
    Project
}
import java.lang {
    Types {
        nativeString
    }
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.vfs {
    VirtualFileManager,
    LocalFileSystem
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument,
    PlatformTextEdit=TextEdit,
    PlatformTextChange=TextChange,
    DefaultCompositeChange
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import com.intellij.psi {
    PsiDocumentManager
}
import ceylon.collection {
    ArrayList
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}

shared class IdeaTextChange(CommonDocument|PhasedUnit|CeylonFile input) satisfies PlatformTextChange {

    value edits = ArrayList<PlatformTextEdit>();

    shared Document doc;
    if (is CommonDocument input) {
        assert (is IdeaDocument input);
        doc = input.nativeDocument;
    } else {
        assert (exists vfile
                    = if (is PhasedUnit input)
                    then VirtualFileManager.instance.findFileByUrl(LocalFileSystem.protocolPrefix + input.unit.fullPath)
                    else input.virtualFile,
                exists d = FileDocumentManager.instance.getDocument(vfile));
        doc = d;
    }

    addEdit(PlatformTextEdit edit) => edits.add(edit);

    shared actual CommonDocument document = IdeaDocument(doc);

    hasEdits => !edits.empty;

    shared actual void initMultiEdit() {}

    shared actual void apply() => applyOnProject();

    shared void applyOnProject(Project? project = null) {
        value markers = edits.collect(
            (c) => doc.createRangeMarker(c.start, c.start + c.length)
        );

        for (change -> marker in zipEntries(edits, markers)) {
            doc.replaceString(marker.startOffset, marker.endOffset, nativeString(change.text));
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
