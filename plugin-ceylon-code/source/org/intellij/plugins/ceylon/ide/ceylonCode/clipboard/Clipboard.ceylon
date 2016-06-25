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
    PsiFile
}
import com.intellij.psi.util {
    PsiUtilBase
}
import com.intellij.ui {
    SimpleTextAttributes
}
import com.redhat.ceylon.ide.common.imports {
    SelectedImportsVisitor,
    pasteImports
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    TypeDeclaration,
    TypedDeclaration
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

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    CeylonCellRenderer {
        Item
    }
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors,
    textAttributes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

DataFlavor flavor = DataFlavor(javaClass<CopiedReferences>(), "Copied References");

shared class CopiedReferences({<Declaration->String>*} references)
        satisfies TextBlockTransferableData {

    value serializableState
            = references.collect((dec->al)
                => [dec.unit.\ipackage.nameAsString, dec.name, al]);

    shared {<Declaration->String>*} resolve(CeylonFile file) {
        if (exists tc = file.localAnalysisResult?.typeChecker) {
            value modules = tc.context.modules.listOfModules;
            return map {
                for ([pname, name, al] in serializableState)
                for (m in modules)
                if (exists p = m.getDirectPackage(pname),
                    exists d = p.getDirectMember(name, null, false))
                d->al
            };
        }
        else {
            return {};
        }
    }

    flavor => package.flavor;

    getOffsets(IntArray ints, Integer i) => 0;
    offsetCount => 0;
    setOffsets(IntArray ints, Integer i) => 0;

}

shared class CeylonCopyPastePostProcessor()
        extends CopyPastePostProcessor<CopiedReferences>() {

    shared actual List<CopiedReferences> collectTransferableData(PsiFile file, Editor editor,
            IntArray startOffsets, IntArray endOffsets) {
        if (is CeylonFile file) {
            value selection = editor.selectionModel;
            value visitor = SelectedImportsVisitor {
                offset = selection.selectionStart;
                length = selection.selectionEnd
                - selection.selectionStart;
            };
            visitor.visit(file.compilationUnit); //TODO: check with David which is correct here
            value result = CopiedReferences(visitor.copiedReferences);
            return Collections.singletonList(result);
        }
        return Collections.emptyList<CopiedReferences>();
    }

    shared actual List<CopiedReferences> extractTransferableData(Transferable content) {
        try {
            return if (is CopiedReferences data = content.getTransferData(flavor))
                then Collections.singletonList(data)
                else Collections.emptyList<CopiedReferences>();
        }
        catch (e) {
            return Collections.emptyList<CopiedReferences>();
        }
    }

    function color(Declaration dec)
            => switch (dec)
            case (is TypeDeclaration)
                textAttributes(ceylonHighlightingColors.type).foregroundColor
            case (is TypedDeclaration)
                textAttributes(ceylonHighlightingColors.identifier).foregroundColor
            else SimpleTextAttributes.regularAttributes.fgColor;

    shared actual void processTransferableData(Project project, Editor editor, RangeMarker bounds,
            Integer caretOffset, Ref<Boolean> indented, List<CopiedReferences> references) {
        if (!references.empty,
            is CeylonFile file = PsiUtilBase.getPsiFileInEditor(editor, project),
            exists reference = references[0]) {
            if (exists phasedUnit = file.localAnalysisResult.lastPhasedUnit) {
                value pack = phasedUnit.\ipackage;
                value unit = phasedUnit.unit;
                value items = {
                    for (dec->al in reference.resolve(file))
                    if (dec.unit.\ipackage != pack,
                        every { for (i in unit.imports) i.declaration != dec })
                    let (p = dec.unit.\ipackage, m = p.\imodule)
                    Item(icons.forDeclaration(dec),
                        color(dec),
                        dec.name,
                        p.nameAsString,
                        "``m.nameAsString`` \"``m.version``\"",
                        dec->al)
                };
                if (!items.empty) {
                    value dialog
                        = PasteImportsDialog(project,
                            items.sort(byIncreasing(Item.label)));
                    dialog.init();
                    if (dialog.showAndGet()) {
                        value insertEdits = pasteImports {
                            references = map {
                                for (it in dialog.selectedElements)
                                if (is Declaration->String entry = it.payload)
                                entry
                            };
                            doc = IdeaDocument(editor.document);
                            rootNode = phasedUnit.compilationUnit;
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
        }
    }

}

shared class CeylonCopyPastePreProcessor() satisfies CopyPastePreProcessor {

    preprocessOnCopy(PsiFile psiFile, IntArray startOffsets, IntArray endOffsets, String text) => text;

    preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String text, RawText rawText) => text;

}