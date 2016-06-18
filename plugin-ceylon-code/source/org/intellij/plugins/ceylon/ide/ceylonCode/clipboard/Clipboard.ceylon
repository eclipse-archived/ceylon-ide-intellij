import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.editorActions {
    CopyPastePreProcessor,
    CopyPastePostProcessor,
    TextBlockTransferableData
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor,
    RawText,
    RangeMarker
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiFile,
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.imports {
    SelectedImportsVisitor,
    pasteImports
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.awt.datatransfer {
    Transferable,
    DataFlavor
}
import java.lang {
    IntArray,
    Boolean
}
import java.util {
    List,
    Collections
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

DataFlavor flavor = DataFlavor(javaClass<CopiedReferences>(), "Copied References");

shared class CopiedReferences(Map<Declaration,String> references)
        satisfies TextBlockTransferableData {

    value serializableState
            = references.collect((dec->al)
                => [dec.unit.\ipackage.nameAsString, dec.name, al]);

    shared Map<Declaration,String> resolve(CeylonFile file) {
        value mod = file.upToDatePhasedUnit.compilationUnit.unit.\ipackage.\imodule;  //TODO: check with David which is correct here
        return map {
            for ([pname, name, al] in serializableState)
            if (exists p = mod.getPackage(pname))
            if (exists d = p.getDirectMember(name, null, false))
                d -> al
        };
    }

    flavor => package.flavor;

    getOffsets(IntArray ints, Integer i) => 0;

    offsetCount => 0;

    setOffsets(IntArray ints, Integer i) => 0;

}

shared class CeylonCopyPastePostProcessor()
        extends CopyPastePostProcessor<CopiedReferences>() {

    shared actual List<CopiedReferences> collectTransferableData(PsiFile psiFile, Editor editor,
            IntArray startOffsets, IntArray endOffsets) {
        if (is CeylonFile psiFile) {
            value selection = editor.selectionModel;
            value visitor = SelectedImportsVisitor {
                offset = selection.selectionStart;
                length = selection.selectionEnd
                - selection.selectionStart;
            };
            visitor.visit(psiFile.upToDatePhasedUnit.compilationUnit); //TODO: check with David which is correct here
            value result = CopiedReferences(visitor.copiedReferences);
            return Collections.singletonList(result);
        }
        return Collections.emptyList<CopiedReferences>();
    }

    shared actual List<CopiedReferences> extractTransferableData(Transferable content)
            => if (is CopiedReferences data = content.getTransferData(flavor))
            then Collections.singletonList(data)
            else Collections.emptyList<CopiedReferences>();

    shared actual void processTransferableData(Project project, Editor editor, RangeMarker bounds,
            Integer caretOffset, Ref<Boolean> indented, List<CopiedReferences> references) {
        if (is CeylonFile file
                = PsiDocumentManager.getInstance(project)
                    .getPsiFile(editor.document),
            exists reference = references[0]) {
            value insertEdits = pasteImports {
                references = reference.resolve(file);
                doc = IdeaDocument(editor.document);
                rootNode = file.compilationUnit;
            };
            if (!insertEdits.empty) {
                object extends WriteCommandAction<Nothing>(file.project, file) {
                    shared actual void run(Result<Nothing> result) {
                        value change = IdeaTextChange(IdeaDocument(editor.document));
                        change.initMultiEdit();
                        insertEdits.each(change.addEdit);
                        change.apply();
                    }
                }.execute();
            }
        }
    }

}

shared class CeylonCopyPastePreProcessor() satisfies CopyPastePreProcessor {

    preprocessOnCopy(PsiFile psiFile, IntArray startOffsets, IntArray endOffsets, String text) => text;

    preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String text, RawText rawText) => text;

}