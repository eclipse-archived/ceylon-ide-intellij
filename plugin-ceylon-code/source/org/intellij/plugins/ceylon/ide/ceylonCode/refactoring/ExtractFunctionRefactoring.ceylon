import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiFile,
    PsiDocumentManager
}
import com.redhat.ceylon.ide.common.refactoring {
    createExtractFunctionRefactoring
}

import java.lang {
    JString=String
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange,
    IdeaCompositeChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ExtractFunctionHandler() extends AbstractExtractHandler() {

    shared actual TextRange? extract(Project myProject, Editor editor, PsiFile file, TextRange range) {
        assert(is CeylonFile file);

        value refacto = createExtractFunctionRefactoring(
            IdeaDocument(editor.document),
            range.startOffset,
            range.endOffset,
            file.compilationUnit,
            file.tokens,
            null,
            empty,
            file.phasedUnit.unitFile
        );

        if (exists refacto) {

            return object extends WriteCommandAction<TextRange?>(myProject, file) {
                shared actual void run(Result<TextRange?> result) {

                    switch (change = refacto.build())
                    case (is IdeaTextChange) {
                        change.applyOnProject(myProject);
                    }
                    case (is IdeaCompositeChange) {
                        change.applyChanges(myProject);
                    }
                    else {}

                    if (exists reg = refacto.decRegion) {
                        value range = TextRange.from(reg.start, reg.length);
                        editor.selectionModel.setSelection(reg.start, reg.end);
                        value newId = editor.document.getText(range);

                        for (dupe in refacto.dupeRegions) {
                            editor.document.replaceString(dupe.start, dupe.start + dupe.length, JString(newId));
                        }

                        result.setResult(range);
                    }

                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                }
            }.execute().resultObject;
        }

        return null;
    }
}
