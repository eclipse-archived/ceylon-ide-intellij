import java.lang {
    JString=String,
    JInteger=Integer
}
import com.intellij.codeInsight.editorActions.enter {
    EnterHandlerDelegate { Result { cont=Continue } }
}
import com.intellij.openapi.actionSystem {
    DataContext
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.editor.actionSystem {
    EditorActionHandler
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.psi.util {
    PsiUtilCore
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTokens
}

shared class EnterInStringHandler() satisfies EnterHandlerDelegate {

    shared actual Result preprocessEnter(PsiFile file, Editor editor,
            Ref<JInteger> caretOffset, Ref<JInteger> caretAdvance,
            DataContext dataContext, EditorActionHandler originalHandler) {
        value el = PsiUtilCore.getElementAtOffset(file, caretOffset.get().intValue());
        value onFirstLine
                = editor.document.getLineNumber(el.textRange.startOffset)
                == editor.document.getLineNumber(editor.caretModel.offset);
        if (el.node.elementType == CeylonTokens.astringLiteral, onFirstLine) {
            editor.document.insertString(editor.caretModel.offset, JString(" "));
        }
        return cont;
    }

    shared actual Result postProcessEnter(PsiFile file, Editor editor, DataContext dataContext) {
        value el = PsiUtilCore.getElementAtOffset(file, editor.caretModel.offset);
        value parentLine = editor.document.getLineNumber(el.textRange.startOffset);
        value caretLine = editor.document.getLineNumber(editor.caretModel.offset);
        value onFirstLine = parentLine + 1 == caretLine;
        value type = el.node.elementType;
        if (onFirstLine,
            type == CeylonTokens.verbatimString
         || type == CeylonTokens.averbatimString
         || type == CeylonTokens.stringLiteral
         || type == CeylonTokens.astringLiteral) {
            value parentOffsetInLine
                    = el.textRange.startOffset
                    - editor.document.getLineStartOffset(parentLine);
            value offsetInLine
                    = editor.caretModel.offset
                    - editor.document.getLineStartOffset(caretLine);
            value nbSpacesToInsert = parentOffsetInLine - offsetInLine + 3;
            editor.document.insertString(editor.caretModel.offset,
                JString(" ".repeat(nbSpacesToInsert)));
        }
        return cont;
    }
}
